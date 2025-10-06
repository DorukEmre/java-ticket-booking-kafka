1) Home Page
  - Upcoming events -> List all events `GET /events`
  - Browse venues -> List all venues `GET /venues`

2) Venues Listing Page
  - Lists all venues `GET /venues`
  - Each venue links to its detail page

3) Venue Detail Page
  - Shows details for a specific venue `GET /venues/{venueId}`
  - Lists events at this venue

4) Events Listing Page
  - Lists all available events `GET /events`
  - Each event links to its detail page

5) Event Detail Page
  - Shows details for a specific event `GET /events/{eventId}`
  - Option to add tickets to cart `POST /cart/{cartId}/items`

6) Cart Page
  - Shows items in the user's cart `GET /cart/{cartId}`
  - Collects customer info (name, email)
  - Allows checkout `POST /cart/{cartId}/checkout`

7) Order Confirmation Page
  - Confirms purchase and shows status `GET /cart/{cartId}/status`
  - Shows order details after successful checkout `GET /orders/{orderId}`

8) User Orders Page
  - Lists all orders for a user `GET /users/{userId}/orders`

9) Admin Page
  - Add Venue   `POST /admin/venues/new`
  - Add Event   `POST /admin/events/new`
  - List all users `GET /admin/users`
  - List all orders `GET /admin/orders`
  - Add User    `POST /admin/user/new`

10) Error/404 Page
