using CatalogService.Entities;
using CatalogService.Events;
using CatalogService.Models;
using CatalogService.Producers;
using CatalogService.Repositories;


namespace CatalogService.Services;

public class InventoryReservationService(
    MessageProducer producer
    // IEventRepository eventRepository
    )
{
    private readonly MessageProducer _producer = producer;
    // private readonly IEventRepository _eventRepository = eventRepository;

    public async Task HandleAsync(InventoryReservationRequested message)
    {
        Console.WriteLine($"Reserving {message.Items.Count} items for order {message.OrderId}");

        List<CartItem> items = message.Items;

        List<long> eventIds = items.Select(item => item.EventId).ToList();
        // List<Event> eventsToUpdate = _eventRepository.FindAllByIdForUpdate(eventIds);

        var reservationResponse = new InventoryReservationResponse
        {
            OrderId = message.OrderId,
            Items = message.Items
        };

        await _producer.ProduceAsync(Topics.INVENTORY_RESERVATION_SUCCEEDED, message.OrderId, reservationResponse);
    }
}
