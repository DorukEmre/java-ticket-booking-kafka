using CatalogService.Responses;

namespace CatalogService.Responses;

public class VenueResponse
{
    public long Id { get; set; }
    public string? Name { get; set; }
    public string? Location { get; set; }
    public int TotalCapacity { get; set; }
    public string? ImageUrl { get; set; }

    public VenueResponse() { }

    public VenueResponse(long id, string name, string location, int totalCapacity, string imageUrl)
    {
        Id = id;
        Name = name;
        Location = location;
        TotalCapacity = totalCapacity;
        ImageUrl = imageUrl;
    }
}
