using Microsoft.EntityFrameworkCore;
using CatalogService.Responses;
using CatalogService.Data;

namespace CatalogService.Services;

public class VenueService
{    
    private readonly CatalogDbContext _context;

    public VenueService(CatalogDbContext context)
    {
        _context = context;
    }

    public async Task<List<VenueResponse>> GetAllVenues()
    {
        return await _context.Venues
            .AsNoTracking()
            .Select(v => new VenueResponse(
                v.Id,
                v.Name,
                v.Location,
                v.TotalCapacity,
                v.ImageUrl
            ))
            .ToListAsync();
    }
}