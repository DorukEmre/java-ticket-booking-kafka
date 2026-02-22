using System;
using System.ComponentModel.DataAnnotations;

namespace CatalogService.Requests;

public class EventCreateRequest
{
    [Required]
    [StringLength(255)]
    public string Name { get; set; } = default!;

    [Range(1, int.MaxValue)]
    public int TotalCapacity { get; set; }

    [Required]
    public long VenueId { get; set; }

    [Required]
    [Range(typeof(decimal), "0", "79228162514264337593543950335")]
    public decimal TicketPrice { get; set; }

    [Required]
    [DataType(DataType.Date)]
    public DateTime EventDate { get; set; }

    [StringLength(1000)]
    public string? Description { get; set; }

    [StringLength(512)]
    public string? ImageUrl { get; set; }
}
