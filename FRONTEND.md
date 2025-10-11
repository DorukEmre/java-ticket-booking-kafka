Frontend documentation for pages and API endpoints

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
    - Can delete cart `DELETE /cart/:cartId`
    - Can update items (`PUT /cart/:cartId/items`)
    - Can delete item `DELETE /cart/:cartId/items`
    - Allows "Proceed to checkout" `POST /cart/{cartId}/checkout`

7) Checkout Page
  - Shows checkout, polling status `GET /cart/{cartId}`
    - Backend checked Order items for price and availability
    - Displays server-validated total price, and any INVALID item 
    - Collects customer info (name, email) (and payment details)
    - Allows mock "Pay now" -> `POST /orders/{orderId}/payment`
      - If OK, delete cart and redirect to Confirmation Page

8) Order Confirmation Page
  - Confirms purchase `GET /orders/{orderId}/confirmation`
  - Shows order details after successful checkout

9) User Orders Page
  - Lists all orders for a user `GET /users/{userId}/orders`

10) Admin Page
  - Add Venue   `POST /admin/venues/new`
  - Add Event   `POST /admin/events/new`
  - List all users `GET /admin/users`
  - List all orders `GET /admin/orders`
  - Add User    `POST /admin/user/new`

11) Error/404 Page

Component architecture

```
App.jsx
│
├── <Navbar />
├── <Routes>
│
│── /  →  <HomePage />
│       ├── <EventList />
│       │     └── <EventCard />
│       └── <VenueList />
│             └── <VenueCard />
│
│── /venues  →  <VenuesPage />
│       └── <VenueList />
│             └── <VenueCard />
│
│── /venues/:venueId  →  <VenueDetailPage />
│       ├── <VenueDetails />
│       └── <EventList />
│             └── <EventCard />
│
│── /events  →  <EventsPage />
│       └── <EventList />
│             └── <EventCard />
│
│── /events/:eventId  →  <EventDetailPage />
│       ├── <EventDetails />
│       └── <AddToCartButton />
│
│── /cart  →  <CartPage />
│       ├── <CartItem /> × N
│       ├── <CartSummary />
│       ├── <CheckoutForm />
│       └── <Button /> (checkout)
│
│── /order/confirmation/:cartId  →  <OrderConfirmationPage />
│       ├── <OrderStatus />
│       └── <OrderSummary />
│
│── /orders  →  <UserOrdersPage />
│       └── <OrderList />
│             └── <OrderCard />
│
│── /admin  →  <AdminPage />
│       ├── <Tabs> (Venues | Events | Users | Orders)
│       ├── VenuesTab
│       │     └── <AdminForm type="venue" />
│       ├── EventsTab
│       │     └── <AdminForm type="event" />
│       ├── UsersTab
│       │     ├── <AdminForm type="user" />
│       │     └── <UserList />
│       └── OrdersTab
│             └── <OrderList />
│
│── *  →  <NotFoundPage />
│       └── <ErrorMessage />
│
└── <Footer />
```