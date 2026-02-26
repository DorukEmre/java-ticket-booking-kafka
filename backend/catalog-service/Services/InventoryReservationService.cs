using CatalogService.Events;
using System;
using System.Threading.Tasks;

namespace CatalogService.Services;

public class InventoryReservationService
{
    public Task HandleAsync(InventoryReservationRequested message)
    {
        Console.WriteLine($"Reserving {message.Items.Count} items for order {message.OrderId}");
        return Task.CompletedTask;
    }
}
