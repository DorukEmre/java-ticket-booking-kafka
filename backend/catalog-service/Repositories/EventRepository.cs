using Microsoft.EntityFrameworkCore;

using CatalogService.Data;
using CatalogService.Entities;


namespace CatalogService.Repositories;

public class EventRepository : IEventRepository
{
    private readonly CatalogDbContext _context;

    public EventRepository(CatalogDbContext context)
    {
        _context = context;
    }

    // sync operations

    public Event? GetEvent(long eventId)
    {
        return _context.Events.Find(eventId);
    }

    // async operations

    public async Task<Event?> GetEventAsync(long eventId)
    {
        return await _context.Events
            .Include(e => e.Venue)
            .SingleOrDefaultAsync(e => e.Id == eventId);
    }

    public async Task<IEnumerable<Event>> GetAllEventsAsync()
    {
        return await _context.Events
            .Include(e => e.Venue)
            .ToListAsync();
    }

    public async Task AddEventAsync(Event eventEntity)
    {
        await _context.Events.AddAsync(eventEntity);
        await _context.SaveChangesAsync();
    }
}
