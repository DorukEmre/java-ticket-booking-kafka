using Microsoft.EntityFrameworkCore;
using CatalogService.Responses;
using CatalogService.Data;

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
        return await _context.Events
            .Include(e => e.Venue)
            .AsNoTracking()
            .Select(e => new EventResponse
            {
                Id = e.Id,
                Name = e.Name,
                Capacity = e.RemainingCapacity,
                Venue = e.Venue,                  // switch to VenueResponse
                TicketPrice = e.TicketPrice,
                EventDate = e.EventDate,
                Description = e.Description,
                ImageUrl = e.ImageUrl
            })
            .ToListAsync();
    }
}