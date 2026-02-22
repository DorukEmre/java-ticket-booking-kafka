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
}