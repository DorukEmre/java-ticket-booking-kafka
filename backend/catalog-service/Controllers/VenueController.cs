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
    public class VenueController : ControllerBase
    {
        private readonly VenueService _venueService;

        public VenueController(VenueService venueService)
        {
            _venueService = venueService;
        }

        [HttpGet("venues")]
        // [SwaggerOperation(Summary = "List all venues", Description = "Lists all available venues.")]
        // [SwaggerResponse(200, "Venues retrieved successfully", typeof(List<VenueResponse>))]
        public async Task<ActionResult<List<VenueResponse>>> GetAllVenues() {
            Console.WriteLine("GET /api/v1/catalog/venues called");

            var venues = await _venueService.GetAllVenues();

            return Ok(venues);
        }

        [HttpGet("venues/{venueId}")]
        // [SwaggerOperation(Summary = "Get venue", Description = "Retrieves venue information by its ID.")]
        // [SwaggerResponse(200, "Venue retrieved successfully", typeof(VenueResponse))]
        // [SwaggerResponse(404, "Venue not found", typeof(ApiErrorResponse))]
        public async Task<ActionResult<VenueResponse>> GetVenueById(long venueId)
        {
            Console.WriteLine($"GET /api/v1/catalog/venues/{venueId} called");

            var venue = await _venueService.GetVenueInformation(venueId);

            return Ok(venue);
        }

        [HttpPost("add-venue")]
        // [SwaggerOperation(Hidden = true, Description = "Admin use. Creates a new venue.")]
        // [SwaggerResponse(201, "Venue created successfully", typeof(VenueResponse))]
        public async Task<ActionResult<VenueResponse>> CreateVenue(
            [FromBody] VenueCreateRequest request)
        {
            Console.WriteLine("POST /api/v1/catalog/add-venue called");

            var createdVenue = await _venueService.CreateVenue(request);

            return CreatedAtAction(nameof(GetVenueById),
                new { venueId = createdVenue.Id },
                createdVenue);
        }
    }
}
