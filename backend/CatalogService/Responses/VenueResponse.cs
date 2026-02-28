namespace CatalogService.Responses;

public class VenueResponse(long id, string name, string location, int totalCapacity, string imageUrl)
{
    public long Id { get; set; } = id;
    public string Name { get; set; } = name;
    public string Location { get; set; } = location;
    public int TotalCapacity { get; set; } = totalCapacity;
    public string ImageUrl { get; set; } = imageUrl;
}
