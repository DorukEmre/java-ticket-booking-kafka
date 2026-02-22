using Microsoft.EntityFrameworkCore;
using CatalogService.Data;
using CatalogService.Entities;
using CatalogService.Requests;
using CatalogService.Responses;

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
        Console.WriteLine("GetAllVenues");

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

    public async Task<VenueResponse> GetVenueInformation(long venueId)
    {
        Console.WriteLine($"Fetching venue information for venueId: {venueId}");

        var venue = await _context.Venues
            .AsNoTracking()
            .Where(v => v.Id == venueId)
            .Select(v => new VenueResponse(
                v.Id,
                v.Name,
                v.Location,
                v.TotalCapacity,
                v.ImageUrl
            ))
            .SingleOrDefaultAsync();

        if (venue == null)
            throw new KeyNotFoundException("Venue not found");

        return venue;
    }

    public async Task<VenueResponse> CreateVenue(VenueCreateRequest request)
    {
        Console.WriteLine($"Creating venue: {request}");

        var venue = new Venue(
            id: 0,
            name: request.Name,
            location: request.Location,
            totalCapacity: request.TotalCapacity,
            imageUrl: string.IsNullOrEmpty(request.ImageUrl) 
              ? "default-venue.jpg" : request.ImageUrl
        );

        _context.Venues.Add(venue);
        await _context.SaveChangesAsync();

        return new VenueResponse(
            venue.Id,
            venue.Name,
            venue.Location,
            venue.TotalCapacity,
            venue.ImageUrl
        );
    }
}