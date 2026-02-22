using Microsoft.EntityFrameworkCore;
using CatalogService.Data;
using CatalogService.Entities;
using CatalogService.Requests;
using CatalogService.Responses;

namespace CatalogService.Services;

public class EventService
{
    private readonly CatalogDbContext _context;

    public EventService(CatalogDbContext context)
    {
        _context = context;
    }

    public async Task<List<EventResponse>> GetAllEvents()
    {
        Console.WriteLine("GetAllEvents");

        return await _context.Events
            .Include(e => e.Venue)
            .AsNoTracking()
            .Select(e => new EventResponse
            (
                e.Id,
                e.Name,
                e.RemainingCapacity,
                e.Venue,
                e.TicketPrice,
                e.EventDate,
                e.Description,
                e.ImageUrl
            ))
            .ToListAsync();
    }

    public async Task<EventResponse?> GetEventInformation(long eventId)
    {
        Console.WriteLine($"Fetching event information for eventId: {eventId}");

        var evt = await _context.Events
            .Include(e => e.Venue)
            .AsNoTracking()
            .Where(e => e.Id == eventId)
            .Select(e => new EventResponse(
                e.Id,
                e.Name,
                e.RemainingCapacity,
                e.Venue,
                e.TicketPrice,
                e.EventDate,
                e.Description,
                e.ImageUrl
            ))
            .SingleOrDefaultAsync();

        if (evt == null)
            throw new KeyNotFoundException("Event not found");

        return evt;
    }

    public async Task<EventResponse> CreateEvent(EventCreateRequest request)
    {
        Console.WriteLine($"Creating event: {request}");

        var venue = await _context.Venues
            .FindAsync(request.VenueId);

        if (venue == null)
            throw new KeyNotFoundException($"Venue with ID {request.VenueId} does not exist.");

        if (request.TotalCapacity > venue.TotalCapacity)
            throw new InvalidOperationException("Event capacity cannot exceed venue capacity.");

        var evt = new Event(
            id: 0,
            name: request.Name,
            totalCapacity: request.TotalCapacity,
            remainingCapacity: request.TotalCapacity,
            ticketPrice: request.TicketPrice,
            eventDate: request.EventDate,
            description: request.Description,
            imageUrl: string.IsNullOrEmpty(request.ImageUrl) 
              ? "default-event.jpg" : request.ImageUrl
        );
        evt.AssignVenue(venue);

        _context.Events.Add(evt);
        await _context.SaveChangesAsync();

        return new EventResponse(
            evt.Id,
            evt.Name,
            evt.RemainingCapacity,
            evt.Venue,
            evt.TicketPrice,
            evt.EventDate,
            evt.Description,
            evt.ImageUrl
        );
    }
}