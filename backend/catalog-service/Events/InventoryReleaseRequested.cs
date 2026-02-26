using CatalogService.Models;

namespace CatalogService.Events;

public class InventoryReleaseRequested
{
    public Guid OrderId { get; set; }
    public List<CartItem> Items { get; set; } = new();
}