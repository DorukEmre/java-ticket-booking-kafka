using CatalogService.Entities;

namespace CatalogService.Repositories;

public interface IEventRepository
{
    // sync operations

    Event? GetEvent(long eventId);

    // async operations

    Task<Event?> GetEventAsync(long eventId);
    Task<IEnumerable<Event>> GetAllEventsAsync();
    Task AddEventAsync(Event eventEntity);

    Task<List<Event>> GetEventsForUpdateAsync(IEnumerable<long> eventIds);
    Task UpdateEventsAsync(IEnumerable<Event> events);
}
