using System.Text.Json;

using Microsoft.EntityFrameworkCore;

using CatalogService.Entities;
using CatalogService.Data;


namespace CatalogService.Resources;

public class PopulateDatabase(CatalogDbContext context)
{
    private readonly CatalogDbContext _context = context;

    private static readonly JsonSerializerOptions JsonOptions = new() { PropertyNameCaseInsensitive = true };


    public async Task SeedAsync()
    {
        // Add venues if there aren't any
        if (!await _context.Venues.AnyAsync())
        {
            await using var stream = File.OpenRead("Resources/venues.json");

            var venues = await JsonSerializer.DeserializeAsync<List<Venue>>(
                stream,
                JsonOptions);

            if (venues is not null)
            {
                await _context.Venues.AddRangeAsync(venues);
                await _context.SaveChangesAsync();
            }
        }

        // Add events if venues exist and events table is empty
        if (await _context.Venues.AnyAsync() && !await _context.Events.AnyAsync())
        {
            var venuesList = await _context.Venues.ToListAsync();

            // Read the JSON as a JsonDocument to handle venueIndex
            using var stream = File.OpenRead("Resources/events.json");
            using var doc = await JsonDocument.ParseAsync(stream);
            var events = new List<Event>();

            foreach (var element in doc.RootElement.EnumerateArray())
            {
                var venueIndex = element.GetProperty("venueIndex").GetInt32();
                var venue = venuesList[venueIndex];

                var evt = new Event(
                    id: 0,
                    name: element.GetProperty("name").GetString()!,
                    totalCapacity: element.GetProperty("totalCapacity").GetInt32(),
                    remainingCapacity: element.GetProperty("totalCapacity").GetInt32(),
                    ticketPrice: element.GetProperty("ticketPrice").GetDecimal(),
                    eventDate: element.GetProperty("eventDate").GetDateTime(),
                    description: element.GetProperty("description").GetString()!,
                    imageUrl: element.GetProperty("imageUrl").GetString()!
                );
                evt.AssignVenue(venue);
                events.Add(evt);

            }

            await _context.Events.AddRangeAsync(events);
            await _context.SaveChangesAsync();
        }
    }
}