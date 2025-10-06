#!/bin/bash

# Add Venues
curl -X POST http://localhost:8000/admin/venues/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Velvet Hall", "location": "Paris", "totalCapacity": 50000}' -w "\n"

curl -X POST http://localhost:8000/admin/venues/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Starlight Venue", "location": "Madrid", "totalCapacity": 8000}' -w "\n"

curl -X POST http://localhost:8000/admin/venues/new \
  -H "Content-Type: application/json" \
  -d '{"name": "The Beacon", "location": "London", "totalCapacity": 20000}' -w "\n"

curl -X POST http://localhost:8000/admin/venues/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Sunset Arena", "location": "Berlin", "totalCapacity": 15000}' -w "\n"

curl -X POST http://localhost:8000/admin/venues/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Echo Dome", "location": "Rome", "totalCapacity": 12000}' -w "\n"

curl -X POST http://localhost:8000/admin/venues/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Harmony Pavilion", "location": "Amsterdam", "totalCapacity": 9000}' -w "\n"

curl -X POST http://localhost:8000/admin/venues/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Crystal Palace", "location": "Vienna", "totalCapacity": 22000}' -w "\n"

curl -X POST http://localhost:8000/admin/venues/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Galaxy Center", "location": "Prague", "totalCapacity": 17000}' -w "\n"

curl -X POST http://localhost:8000/admin/venues/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Nova Hall", "location": "Budapest", "totalCapacity": 14000}' -w "\n"

curl -X POST http://localhost:8000/admin/venues/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Opal Theater", "location": "Brussels", "totalCapacity": 11000}' -w "\n"

# Add Events
curl -X POST http://localhost:8000/admin/events/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Neon Nights Festival", "totalCapacity": 50000, "venueId": 1, "ticketPrice": 120, "description": "A dazzling festival of lights and music, featuring top international DJs and immersive visual experiences.", "eventDate": "2025-01-17"}' -w "\n"

curl -X POST http://localhost:8000/admin/events/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Pulse Concert Series", "totalCapacity": 40000, "venueId": 1, "ticketPrice": 130, "description": "An electrifying concert series with high-energy performances and a vibrant crowd.", "eventDate": "2025-02-12"}' -w "\n"

curl -X POST http://localhost:8000/admin/events/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Rhythm & Beats Gala", "totalCapacity": 8000, "venueId": 2, "ticketPrice": 90, "description": "A night dedicated to rhythm, featuring live bands and dance performances.", "eventDate": "2025-03-08"}' -w "\n"

curl -X POST http://localhost:8000/admin/events/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Starlit Soirée", "totalCapacity": 8000, "venueId": 2, "ticketPrice": 100, "description": "An elegant evening under the stars with classical music and gourmet dining.", "eventDate": "2025-04-19"}' -w "\n"

curl -X POST http://localhost:8000/admin/events/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Aurora Live", "totalCapacity": 20000, "venueId": 3, "ticketPrice": 110, "description": "Experience the magic of live performances inspired by the northern lights.", "eventDate": "2025-05-23"}' -w "\n"

curl -X POST http://localhost:8000/admin/events/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Echoes of Tomorrow", "totalCapacity": 15000, "venueId": 4, "ticketPrice": 95, "description": "A futuristic music event blending electronic sounds and innovative stage design.", "eventDate": "2025-06-14"}' -w "\n"

curl -X POST http://localhost:8000/admin/events/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Sunset Groove", "totalCapacity": 15000, "venueId": 6, "ticketPrice": 105, "description": "Chill out with sunset views and groovy tunes from renowned artists.", "eventDate": "2025-07-10"}' -w "\n"

curl -X POST http://localhost:8000/admin/events/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Dome Sessions", "totalCapacity": 12000, "venueId": 5, "ticketPrice": 80, "description": "Intimate live sessions inside the iconic dome, featuring acoustic sets.", "eventDate": "2025-08-05"}' -w "\n"

curl -X POST http://localhost:8000/admin/events/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Roman Nights", "totalCapacity": 12000, "venueId": 5, "ticketPrice": 85, "description": "A celebration of Roman culture with music, food, and historical performances.", "eventDate": "2025-09-22"}' -w "\n"

curl -X POST http://localhost:8000/admin/events/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Harmony Fest", "totalCapacity": 9000, "venueId": 6, "ticketPrice": 70, "description": "A festival promoting peace and unity through diverse musical acts.", "eventDate": "2025-10-13"}' -w "\n"

curl -X POST http://localhost:8000/admin/events/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Pavilion Party", "totalCapacity": 9000, "venueId": 6, "ticketPrice": 75, "description": "A lively party with dance music, food trucks, and interactive art installations.", "eventDate": "2025-11-21"}' -w "\n"

curl -X POST http://localhost:8000/admin/events/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Crystal Ball", "totalCapacity": 22000, "venueId": 7, "ticketPrice": 115, "description": "A glamorous masquerade ball with live orchestras and ballroom dancing.", "eventDate": "2025-12-18"}' -w "\n"

curl -X POST http://localhost:8000/admin/events/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Palace Parade", "totalCapacity": 22000, "venueId": 8, "ticketPrice": 120, "description": "A grand parade with floats, performers, and fireworks at the palace.", "eventDate": "2025-01-29"}' -w "\n"

curl -X POST http://localhost:8000/admin/events/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Galaxy Gathering", "totalCapacity": 17000, "venueId": 8, "ticketPrice": 100, "description": "A cosmic-themed gathering with space-inspired music and visuals.", "eventDate": "2025-03-15"}' -w "\n"

curl -X POST http://localhost:8000/admin/events/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Center Stage", "totalCapacity": 17000, "venueId": 8, "ticketPrice": 105, "description": "A showcase of emerging artists and bands on the main stage.", "eventDate": "2025-05-27"}' -w "\n"

curl -X POST http://localhost:8000/admin/events/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Nova Nights", "totalCapacity": 14000, "venueId": 9, "ticketPrice": 90, "description": "A night of indie music and stargazing.", "eventDate": "2025-07-03"}' -w "\n"

curl -X POST http://localhost:8000/admin/events/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Nova Vibes", "totalCapacity": 14000, "venueId": 9, "ticketPrice": 95, "description": "A celebration of the vibrant music scene with local and international acts.", "eventDate": "2025-09-09"}' -w "\n"

curl -X POST http://localhost:8000/admin/events/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Opal Opening", "totalCapacity": 11000, "venueId": 10, "ticketPrice": 85, "description": "The grand opening of Opal Theater with a star-studded lineup.", "eventDate": "2025-10-25"}' -w "\n"

curl -X POST http://localhost:8000/admin/events/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Beats Bash", "totalCapacity": 11000, "venueId": 10, "ticketPrice": 90, "description": "A high-energy bash featuring DJs, live acts, and street food.", "eventDate": "2025-02-28"}' -w "\n"

curl -X POST http://localhost:8000/admin/events/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Grand Encore", "totalCapacity": 50000, "venueId": 10, "ticketPrice": 140, "description": "A spectacular encore event bringing together the best performers for one night only.", "eventDate": "2025-06-30"}' -w "\n"
