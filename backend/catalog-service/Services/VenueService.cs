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
            .Select(e => new VenueResponse
            {
                Id = e.Id,
                Name = e.Name,
                Location = e.Location,
                TotalCapacity = e.TotalCapacity,
                ImageUrl = e.ImageUrl
            })
            .ToListAsync();
    }
}