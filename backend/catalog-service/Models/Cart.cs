using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

namespace CatalogService.Models;

public class Cart
{
    public Guid CartId { get; set; }
    public List<CartItem> Items { get; set; } = new();
}

public class CartItem
{
    public long EventId { get; set; }
    public int TicketCount { get; set; }
    public decimal TicketPrice { get; set; }

    public decimal? PreviousPrice { get; set; }
    public bool PriceChanged { get; set; }
    public bool Unavailable { get; set; }
}