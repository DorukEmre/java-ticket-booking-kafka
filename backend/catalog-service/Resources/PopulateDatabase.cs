using CatalogService.Entities;
using CatalogService.Data;
using Microsoft.EntityFrameworkCore;
using System.Text.Json;

namespace CatalogService.Resources;

public class PopulateDatabase
{
    private readonly CatalogDbContext _context;

    public PopulateDatabase(CatalogDbContext context)
    {
        _context = context;
    }

    public async Task SeedAsync()
    {
        // Add venues if there aren't any
        if (!await _context.Venues.AnyAsync())
        {
            var venues = await JsonSerializer.DeserializeAsync<List<Venue>>(
                File.OpenRead("Resources/venues.json"),
                new JsonSerializerOptions { PropertyNameCaseInsensitive = true }
            );

            if (venues != null)
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

                events.Add(new Event
                {
                    Name = element.GetProperty("name").GetString()!,
                    TotalCapacity = element.GetProperty("totalCapacity").GetInt32(),
                    RemainingCapacity = element.GetProperty("totalCapacity").GetInt32(),
                    Venue = venue,
                    TicketPrice = element.GetProperty("ticketPrice").GetDecimal(),
                    Description = element.GetProperty("description").GetString(),
                    EventDate = element.GetProperty("eventDate").GetDateTime(),
                    ImageUrl = element.GetProperty("imageUrl").GetString()
                });
            }

            await _context.Events.AddRangeAsync(events);
            await _context.SaveChangesAsync();
        }
    }
}