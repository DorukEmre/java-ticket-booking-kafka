using System.ComponentModel.DataAnnotations;

namespace CatalogService.Requests;

public class VenueCreateRequest
{
    [Required]
    [StringLength(255)]
    public string Name { get; set; } = default!;

    [Required]
    [StringLength(255)]
    public string Location { get; set; } = default!;

    [Range(1, int.MaxValue)]
    public int TotalCapacity { get; set; }

    [StringLength(512)]
    public string? ImageUrl { get; set; }
}
