using CatalogService.Models;

namespace CatalogService.Events;

public class ReserveInventory
{
    public Guid OrderId { get; set; }
    public List<CartItem> Items { get; set; } = new();
}