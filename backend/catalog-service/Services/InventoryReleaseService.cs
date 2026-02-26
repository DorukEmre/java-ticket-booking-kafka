using CatalogService.Events;
using System;
using System.Threading.Tasks;

namespace CatalogService.Services;

public class InventoryReleaseService
{
    public Task HandleAsync(InventoryReleaseRequested message)
    {
        Console.WriteLine($"Releasing {message.Items.Count} items for order {message.OrderId}");
        return Task.CompletedTask;
    }
}
