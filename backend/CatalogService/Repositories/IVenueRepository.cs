using CatalogService.Entities;

namespace CatalogService.Repositories;

public interface IVenueRepository
{
    // async operations

    Task<Venue?> GetVenueAsync(long venueId);
    Task<IEnumerable<Venue>> GetAllVenuesAsync();
    Task AddVenueAsync(Venue venueEntity);
}
