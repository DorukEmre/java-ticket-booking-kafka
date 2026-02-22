using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace CatalogService.Entities;

[Table("event")]
public class Event
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    [Column("id")]
    public long Id { get; set; }

    [Column("name")]
    [Required(ErrorMessage = "Event name must not be blank")]
    [StringLength(255, ErrorMessage = "Event name must not exceed 255 characters")]
    public string Name { get; set; } = null!;

    [Column("total_capacity")]
    [Range(1, int.MaxValue, ErrorMessage = "Total capacity must be a positive number")]
    public int TotalCapacity { get; set; }

    [Column("remaining_capacity")]
    [Range(0, int.MaxValue, ErrorMessage = "Remaining capacity must be zero or a positive number")]
    public int RemainingCapacity { get; set; }

    [Column("venue_id")]
    public long VenueId { get; set; }

    [ForeignKey(nameof(VenueId))]
    public Venue Venue { get; set; } = null!;
    
    [Column("ticket_price")]
    [Required(ErrorMessage = "Ticket price must not be null")]
    [Range(0.0, double.MaxValue, ErrorMessage = "Ticket price must be at least 0.0")]
    public decimal TicketPrice { get; set; }

    [Column("event_date")]
    [Required(ErrorMessage = "Event date must not be null")]
    public DateTime EventDate { get; set; }

    [Column("description")]
    [StringLength(1000, ErrorMessage = "Description must not exceed 1000 characters")]
    public string Description { get; set; } = null!;

    [Column("image_url")]
    [StringLength(512, ErrorMessage = "Image URL must not exceed 512 characters")]
    public string ImageUrl { get; set; } = null!;

    private Event() { }

    public Event(long id, string name, int totalCapacity, int remainingCapacity, decimal ticketPrice, DateTime eventDate, string description, string imageUrl)
    {
        Id = id;
        Name = name;
        TotalCapacity = totalCapacity;
        RemainingCapacity = remainingCapacity;
        TicketPrice = ticketPrice;
        EventDate = eventDate;
        Description = description;
        ImageUrl = imageUrl;

        PrePersist();
    }

    public void AssignVenue(Venue venue)
    {
        Venue = venue;
    }

    private void PrePersist()
    {
        if (RemainingCapacity == 0)
        {
            RemainingCapacity = TotalCapacity;
        }
    }
}
