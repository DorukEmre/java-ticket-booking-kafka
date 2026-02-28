using Microsoft.EntityFrameworkCore;

using CatalogService.Models;
using CatalogService.Repositories;

namespace CatalogService.Services;

public class InventoryService(IEventRepository eventRepository)
{
    private readonly IEventRepository _eventRepository = eventRepository;


    // Validate cart from cart-service
    public Dictionary<long, bool> ValidateCart(Cart cart)
    {
        Console.WriteLine($"Validating cart with {cart.Items.Count} items.");

        var result = new Dictionary<long, bool>();

        // Check if each item is a valid event and has enough capacity
        foreach (var item in cart.Items)
        {
            var evt = _eventRepository.GetEvent(item.EventId);

            bool isValid = evt != null
                          && item.TicketCount > 0
                          && evt.RemainingCapacity >= item.TicketCount;

            Console.WriteLine(
              $"item.EventId {item.EventId} is valid: {isValid}. " +
              $"Event null? {(evt == null)}, " +
              $"remaining capacity: {(evt != null ? evt.RemainingCapacity.ToString() : "N/A")}, " +
              $"requested: {item.TicketCount}");

            // isValid = true; // TEMPORARY FOR TESTING

            result[item.EventId] = isValid;
        }

        return result;
    }
}