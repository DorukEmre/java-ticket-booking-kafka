using CatalogService.Models;

namespace CatalogService.Events;

public class InventoryReservationRequested
{
    public required string OrderId { get; set; }
    public List<CartItem> Items { get; set; } = new();
}