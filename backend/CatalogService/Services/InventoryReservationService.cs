using System.Text.Json;

using CatalogService.Data;
using CatalogService.Entities;
using CatalogService.Events;
using CatalogService.Models;
using CatalogService.Producers;
using CatalogService.Repositories;


namespace CatalogService.Services;

public class InventoryReservationService(
    MessageProducer producer,
    IServiceProvider serviceProvider,
    ILogger<InventoryReservationService> logger
    )
{
    private readonly MessageProducer _producer = producer;
    private readonly IServiceProvider _serviceProvider = serviceProvider;
    private readonly ILogger<InventoryReservationService> _logger = logger;


    public async Task HandleAsync(InventoryReservationRequested message)
    {
        Console.WriteLine($"Reserving {message.Items.Count} items for order {message.OrderId}");

        // Create scoped repository
        using var scope = _serviceProvider.CreateScope();
        var eventRepository = scope.ServiceProvider.GetRequiredService<IEventRepository>();
        var dbContext = scope.ServiceProvider.GetRequiredService<CatalogDbContext>();

        // Start a transaction to lock the selected rows with FOR UPDATE
        await using var transaction = await dbContext.Database.BeginTransactionAsync();

        try
        {
            List<long> eventIds = message.Items.Select(i => i.EventId).ToList();
            List<Event> eventsToUpdate = await eventRepository.GetEventsForUpdateAsync(eventIds);

            List<CartItem> validatedItems = [];
            bool eventMissing = false;

            // Validate each item by checking that the price matches the one in the db and availabilty
            foreach (CartItem item in message.Items)
            {
                if (eventsToUpdate.FirstOrDefault(e => e.Id == item.EventId) is Event evt)
                {
                    // Check price change
                    decimal previousPrice = item.TicketPrice ?? 0m;
                    if (item.EventId == 8) // FOR TESTING, force price change
                        previousPrice += 10m;

                    decimal currentPrice = evt.TicketPrice;
                    bool priceChanged = (previousPrice != currentPrice);

                    // Check capacity
                    bool unavailable = evt.RemainingCapacity < item.TicketCount;
                    if (item.EventId == 7) // FOR TESTING, force unavailable
                        unavailable = true;

                    CartItem updated = new()
                    {
                        EventId = item.EventId,
                        TicketCount = item.TicketCount,
                        PreviousPrice = previousPrice,
                        TicketPrice = currentPrice,
                        PriceChanged = priceChanged,
                        Unavailable = unavailable
                    };

                    validatedItems.Add(updated);
                }
                else
                {
                    eventMissing = true;
                }
            }

            // Create response content
            InventoryReservationResponse reservationResponse = new()
            {
                OrderId = message.OrderId,
                Items = validatedItems
            };

            // Check all items have a ticket price and an eventId
            bool allHavePrice = validatedItems
                .All(i => i.TicketPrice.HasValue && i.TicketPrice.Value > 0m);
            bool allHaveEventId = validatedItems
                .All(i => i.EventId != 0);

            if (eventMissing || !allHavePrice || !allHaveEventId)
            {
                _logger.LogWarning("Reservation failed due to database error: {ValidatedItems}", JsonSerializer.Serialize(validatedItems));

                await _producer.ProduceAsync(
                    Topics.INVENTORY_RESERVATION_FAILED,
                    message.OrderId,
                    reservationResponse);

                return;
            }

            // Check all items are valid: !Unavailable and !PriceChanged
            bool allValid = validatedItems
                .All(i => !i.Unavailable && !i.PriceChanged);

            // If not all valid, send INVENTORY_RESERVATION_INVALID and return
            if (!allValid)
            {
                _logger.LogWarning("Reservation invalid: {ValidatedItems}", JsonSerializer.Serialize(validatedItems));

                await _producer.ProduceAsync(
                    Topics.INVENTORY_RESERVATION_INVALID,
                    message.OrderId,
                    reservationResponse
                );

                return;
            }

            // If all valid, update each event's remaining capacity
            foreach (CartItem item in validatedItems)
            {
                if (eventsToUpdate.FirstOrDefault(e => e.Id == item.EventId) is Event evt)
                {
                    evt.RemainingCapacity -= item.TicketCount;
                }
            }

            // Save changes to db and commit
            await eventRepository.UpdateEventsAsync(eventsToUpdate);
            await transaction.CommitAsync();

            _logger.LogInformation(
                "Successfully reserved inventory for order {OrderId}",
                message.OrderId
            );

            // Produce success message
            await _producer.ProduceAsync(
                Topics.INVENTORY_RESERVATION_SUCCEEDED,
                message.OrderId,
                reservationResponse
            );

        }
        catch
        {
            await transaction.RollbackAsync();
            throw;
        }
    }
}
