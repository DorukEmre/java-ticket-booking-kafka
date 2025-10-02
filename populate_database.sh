#!/bin/bash

# Add Venues
curl -X POST http://localhost:8000/venue/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Velvet Hall", "address": "Paris", "totalCapacity": 50000}' -w "\n"

curl -X POST http://localhost:8000/venue/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Starlight Venue", "address": "Madrid", "totalCapacity": 8000}' -w "\n"

curl -X POST http://localhost:8000/venue/new \
  -H "Content-Type: application/json" \
  -d '{"name": "The Beacon", "address": "London", "totalCapacity": 20000}' -w "\n"


# Add Events
curl -X POST http://localhost:8000/event/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Neon Nights Festival", "totalCapacity": 50000, "venueId": 1, "ticketPrice": 120, "description": "This is a really cool event", "eventDate": "2025-11-15"}' -w "\n"

curl -X POST http://localhost:8000/event/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Pulse Concert Series", "totalCapacity": 40000, "venueId": 1, "ticketPrice": 130, "description": "This is a really cool event", "eventDate": "2025-11-15"}' -w "\n"

curl -X POST http://localhost:8000/event/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Rhythm & Beats Gala", "totalCapacity": 8000, "venueId": 2, "ticketPrice": 90, "description": "This is a really cool event", "eventDate": "2025-11-15"}' -w "\n"

curl -X POST http://localhost:8000/event/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Starlit Soirée", "totalCapacity": 8000, "venueId": 2, "ticketPrice": 100, "description": "This is a really cool event", "eventDate": "2025-11-15"}' -w "\n"

curl -X POST http://localhost:8000/event/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Aurora Live", "totalCapacity": 20000, "venueId": 3, "ticketPrice": 110, "description": "This is a really cool event", "eventDate": "2025-11-15"}' -w "\n"

# Add Users
curl -X POST http://localhost:8000/users/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Alice", "email": "alice@email.com"}' -w "\n"

curl -X POST http://localhost:8000/users/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Tom", "email": "tom@email.com"}' -w "\n"

curl -X POST http://localhost:8000/users/new \
  -H "Content-Type: application/json" \
  -d '{"name": "Bob", "email": "bob@email.com"}' -w "\n"
