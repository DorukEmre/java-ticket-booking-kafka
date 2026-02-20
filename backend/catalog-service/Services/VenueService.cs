using System.Collections.Generic;
using CatalogService.Responses;

namespace CatalogService.Services
{
    public class VenueService
    {
        public List<VenueResponse> GetAllVenues()
        {
            return new List<VenueResponse>
            {
                new VenueResponse { Id = 1, Name = "Venue 1", Location = "Paris" },
                new VenueResponse { Id = 2, Name = "Venue 2", Location = "Madrid" }
            };
        }
    }
}
