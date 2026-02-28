using Microsoft.EntityFrameworkCore;

using CatalogService.Entities;
using CatalogService.Repositories;
using CatalogService.Requests;
using CatalogService.Responses;

namespace CatalogService.Services;

public class VenueService
{
    private readonly IVenueRepository _venueRepository;

    public VenueService(IVenueRepository venueRepository)
    {
        _venueRepository = venueRepository;
    }

    public async Task<List<VenueResponse>> GetAllVenues()
    {
        Console.WriteLine("GetAllVenues");

        var venues = await _venueRepository.GetAllVenuesAsync();

        return venues
            .Select(v => new VenueResponse(
                v.Id,
                v.Name,
                v.Location,
                v.TotalCapacity,
                v.ImageUrl
            ))
            .ToList();
    }

    public async Task<VenueResponse> GetVenueInformation(long venueId)
    {
        Console.WriteLine($"Fetching venue information for venueId: {venueId}");

        var venue = await _venueRepository.GetVenueAsync(venueId)
            ?? throw new KeyNotFoundException("Venue not found");

        return new VenueResponse(
                venue.Id,
                venue.Name,
                venue.Location,
                venue.TotalCapacity,
                venue.ImageUrl
            );
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

        await _venueRepository.AddVenueAsync(venue);

        return new VenueResponse(
            venue.Id,
            venue.Name,
            venue.Location,
            venue.TotalCapacity,
            venue.ImageUrl
        );
    }
}