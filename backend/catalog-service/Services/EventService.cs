using System.Collections.Generic;
using CatalogService.Responses;

namespace CatalogService.Services
{
    public class EventService
    {
        public List<EventResponse> GetAllEvents()
        {
            return new List<EventResponse>
            {
                new EventResponse { Id = 1, Name = "Event 1", EventDate = DateTime.Now },
                new EventResponse { Id = 2, Name = "Event 2", EventDate = DateTime.Now.AddDays(1) }
            };
        }
    }
}
