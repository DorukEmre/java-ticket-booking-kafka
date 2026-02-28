using CatalogService.Entities;


namespace CatalogService.Responses;

public class EventResponse(
    long id, string name, int capacity, Venue venue, decimal ticketPrice, DateTime eventDate, string description, string imageUrl)
{
    public long Id { get; set; } = id;
    public string Name { get; set; } = name;
    public int Capacity { get; set; } = capacity;
    public Venue Venue { get; set; } = venue;
    public decimal TicketPrice { get; set; } = ticketPrice;
    public DateTime EventDate { get; set; } = eventDate;
    public string Description { get; set; } = description;
    public string ImageUrl { get; set; } = imageUrl;
}
