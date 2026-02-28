using System;
using System.Threading.Tasks;

using CatalogService.Events;


namespace CatalogService.Services;

public class InventoryReleaseService
{
    public Task HandleAsync(InventoryReleaseRequested message)
    {
        Console.WriteLine($"Releasing {message.Items.Count} items for order {message.OrderId}");
        return Task.CompletedTask;
    }
}
