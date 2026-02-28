using Microsoft.EntityFrameworkCore;
using MySql.Data.MySqlClient;

using CatalogService.Data;
using CatalogService.Entities;


namespace CatalogService.Repositories;

public class EventRepository(CatalogDbContext context) : IEventRepository
{
    private readonly CatalogDbContext _context = context;


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

    public async Task<List<Event>> GetEventsForUpdateAsync(IEnumerable<long> eventIds)
    {
        var idList = eventIds.ToList();

        var events = await _context.Events
            .Where(e => idList.Contains(e.Id))
            .ToListAsync();

        // Apply FOR UPDATE for row-level lock
        await _context.Events
            .FromSqlRaw($"SELECT * FROM event WHERE Id IN ({string.Join(',', idList)}) FOR UPDATE")
            .ToListAsync();

        return events;
    }

    public async Task UpdateEventsAsync(IEnumerable<Event> events)
    {
        _context.Events.UpdateRange(events);   // entities marked as modified
        await _context.SaveChangesAsync();
    }
}
