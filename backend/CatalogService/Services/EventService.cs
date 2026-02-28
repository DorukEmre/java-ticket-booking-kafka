using Microsoft.EntityFrameworkCore;

using CatalogService.Entities;
using CatalogService.Repositories;
using CatalogService.Requests;
using CatalogService.Responses;

namespace CatalogService.Services;

public class EventService(
        IEventRepository eventRepository,
        IVenueRepository venueRepository)
{
    private readonly IEventRepository _eventRepository = eventRepository;
    private readonly IVenueRepository _venueRepository = venueRepository;


    public async Task<List<EventResponse>> GetAllEvents()
    {
        Console.WriteLine("GetAllEvents");

        var events = await _eventRepository.GetAllEventsAsync();

        return events
                .Select(evt => new EventResponse
                (
                    evt.Id,
                    evt.Name,
                    evt.RemainingCapacity,
                    evt.Venue,
                    evt.TicketPrice,
                    evt.EventDate,
                    evt.Description,
                    evt.ImageUrl
                ))
                .ToList();
    }

    public async Task<EventResponse?> GetEventInformation(long eventId)
    {
        Console.WriteLine($"Fetching event information for eventId: {eventId}");

        var evt = await _eventRepository.GetEventAsync(eventId)
            ?? throw new KeyNotFoundException("Event not found");

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


    public async Task<EventResponse> CreateEvent(EventCreateRequest request)
    {
        Console.WriteLine($"Creating event: {request}");

        var venue = await _venueRepository.GetVenueAsync(request.VenueId);

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
            description: request.Description ?? "",
            imageUrl: string.IsNullOrEmpty(request.ImageUrl)
              ? "default-event.jpg" : request.ImageUrl
        );
        evt.AssignVenue(venue);

        await _eventRepository.AddEventAsync(evt);

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