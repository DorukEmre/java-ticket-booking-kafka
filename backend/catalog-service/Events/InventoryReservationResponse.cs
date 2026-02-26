using CatalogService.Models;

namespace CatalogService.Events;

public class InventoryReservationResponse
{
    public Guid OrderId { get; set; }
    public List<CartItem> Items { get; set; } = new();
}