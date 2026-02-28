using Microsoft.EntityFrameworkCore;

using CatalogService.Data;
using CatalogService.Entities;


namespace CatalogService.Repositories;

public class VenueRepository : IVenueRepository
{
    private readonly CatalogDbContext _context;

    public VenueRepository(CatalogDbContext context)
    {
        _context = context;
    }

    // async operations

    public async Task<Venue?> GetVenueAsync(long venueId)
    {
        return await _context.Venues.FindAsync(venueId);
    }

    public async Task<IEnumerable<Venue>> GetAllVenuesAsync()
    {
        return await _context.Venues
            .ToListAsync();
    }

    public async Task AddVenueAsync(Venue venueEntity)
    {
        await _context.Venues.AddAsync(venueEntity);
        await _context.SaveChangesAsync();
    }
}
