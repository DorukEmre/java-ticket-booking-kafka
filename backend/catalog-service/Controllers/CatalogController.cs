using Microsoft.AspNetCore.Mvc;
// using Swashbuckle.AspNetCore.Annotations;
using CatalogService.Entities;
using CatalogService.Responses;
using CatalogService.Services;
using System.Collections.Generic;

namespace CatalogService.Controllers
{
    [ApiController]
    [Route("api/v1/catalog")]
    public class CatalogController : ControllerBase
    {
        private readonly EventService _eventService;
        private readonly VenueService _venueService;

        public CatalogController(EventService eventService, VenueService venueService)
        {
            _eventService = eventService;
            _venueService = venueService;
        }

        [HttpGet("events")]
        // [SwaggerOperation(Summary = "List all events", Description = "Lists all available events.")]
        // [SwaggerResponse(200, "Events retrieved successfully", typeof(List<EventResponse>))]
        public async Task<ActionResult<List<EventResponse>>> GetAllEvents() {
            Console.WriteLine("GET /api/v1/catalog/events called");

            var events = await _eventService.GetAllEvents();
            return Ok(events);
        }

        [HttpGet("venues")]
        // [SwaggerOperation(Summary = "List all venues", Description = "Lists all available venues.")]
        // [SwaggerResponse(200, "Venues retrieved successfully", typeof(List<VenueResponse>))]
        public async Task<ActionResult<List<VenueResponse>>> GetAllVenues() {
            Console.WriteLine("GET /api/v1/catalog/venues called");

            var venues = await _venueService.GetAllVenues();
            return Ok(venues);
        }
    }
}
