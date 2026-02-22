using Microsoft.AspNetCore.Mvc;
// using Swashbuckle.AspNetCore.Annotations;
using CatalogService.Entities;
using CatalogService.Requests;
using CatalogService.Responses;
using CatalogService.Services;
using System.Collections.Generic;

namespace CatalogService.Controllers
{
    [ApiController]
    [Route("api/v1/catalog")]
    public class EventController : ControllerBase
    {
        private readonly EventService _eventService;

        public EventController(EventService eventService)
        {
            _eventService = eventService;
        }

        [HttpGet("events")]
        // [SwaggerOperation(Summary = "List all events", Description = "Lists all available events.")]
        // [SwaggerResponse(200, "Events retrieved successfully", typeof(List<EventResponse>))]
        public async Task<ActionResult<List<EventResponse>>> GetAllEvents() {
            Console.WriteLine("GET /api/v1/catalog/events called");

            var events = await _eventService.GetAllEvents();

            return Ok(events);
        }

        [HttpGet("events/{eventId}")]
        // [SwaggerOperation(Summary = "Get event", Description = "Retrieves event information by its ID.")]
        // [SwaggerResponse(200, "Event retrieved successfully", typeof(EventResponse))]
        // [SwaggerResponse(404, "Event not found", typeof(ApiErrorResponse))]
        public async Task<ActionResult<EventResponse>> GetEventById(long eventId)
        {
            Console.WriteLine($"GET /api/v1/catalog/events/{eventId} called");

            var evt = await _eventService.GetEventInformation(eventId);

            return Ok(evt);
        }

        [HttpPost("add-event")]
        // [SwaggerOperation(Hidden = true, Description = "Admin use. Creates a new event.")]
        // [SwaggerResponse(201, "Event created successfully", typeof(EventResponse))]
        public async Task<ActionResult<EventResponse>> CreateEvent(
            [FromBody] EventCreateRequest request)
        {
            Console.WriteLine("POST /api/v1/catalog/add-event called");

            var createdEvent = await _eventService.CreateEvent(request);

            return CreatedAtAction(nameof(GetEventById),
                new { eventId = createdEvent.Id },
                createdEvent);
        }
    }
}
