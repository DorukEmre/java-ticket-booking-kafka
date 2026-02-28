using CatalogService.Data;
using CatalogService.Entities;
using CatalogService.Events;
using CatalogService.Models;
using CatalogService.Repositories;


namespace CatalogService.Services;

public class InventoryReleaseService(
    IServiceProvider serviceProvider,
    ILogger<InventoryReleaseService> logger)
{
    private readonly IServiceProvider _serviceProvider = serviceProvider;
    private readonly ILogger<InventoryReleaseService> _logger = logger;


    public async Task HandleAsync(InventoryReleaseRequested message)
    {
        _logger.LogInformation("Releasing {Count} items for order {OrderId}", message.Items.Count, message.OrderId);

        // Create a scope to get repository
        using var scope = _serviceProvider.CreateScope();
        var eventRepository = scope.ServiceProvider.GetRequiredService<IEventRepository>();
        var dbContext = scope.ServiceProvider.GetRequiredService<CatalogDbContext>();

        // Start a transaction for row-level locks
        await using var transaction = await dbContext.Database.BeginTransactionAsync();

        // Fetch events with FOR UPDATE to lock them
        var eventIds = message.Items.Select(i => i.EventId).ToList();
        var eventsToUpdate = await eventRepository.GetEventsForUpdateAsync(eventIds);

        // Update remaining capacities by adding back the ticketCount
        foreach (CartItem item in message.Items)
        {
            if (eventsToUpdate.FirstOrDefault(e => e.Id == item.EventId) is Event evt)
            {
                evt.RemainingCapacity += item.TicketCount;

                // Do not exceed total capacity
                if (evt.RemainingCapacity > evt.TotalCapacity)
                {
                    evt.RemainingCapacity = evt.TotalCapacity;
                    _logger.LogWarning("Adjusted remaining capacity for event {EventId} to not exceed total capacity.", evt.Id);
                }

                _logger.LogInformation(
                    "Released {Count} tickets for event {EventId}. New remaining capacity: {Capacity}",
                    item.TicketCount, evt.Id, evt.RemainingCapacity);
            }
        }

        // Save updates to db and commit transaction
        await eventRepository.UpdateEventsAsync(eventsToUpdate);
        await transaction.CommitAsync();
    }
}