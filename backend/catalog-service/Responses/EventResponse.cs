using System;
using System.Numerics;
using CatalogService.Entities;

namespace CatalogService.Responses;

public class EventResponse
{
    public long Id { get; set; }
    public string? Name { get; set; }
    public int? Capacity { get; set; }
    public Venue? Venue { get; set; }
    public decimal? TicketPrice { get; set; }
    public DateTime? EventDate { get; set; } // keep as string?
    public string? Description { get; set; }
    public string? ImageUrl { get; set; }

    public EventResponse() { }

    public EventResponse(long id, string name, int capacity, Venue venue, decimal ticketPrice, DateTime eventDate, string description, string imageUrl)
    {
        Id = id;
        Name = name;
        Capacity = capacity;
        Venue = venue;
        TicketPrice = ticketPrice;
        EventDate = eventDate;
        Description = description;
        ImageUrl = imageUrl;
    }
}
