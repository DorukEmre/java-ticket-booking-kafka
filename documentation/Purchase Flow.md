## Purchase Flow
Ticket purchase request flow through the system. Each service communicates via Kafka events to ensure reliable and decoupled processing.

1) Frontend (`/cart`) → Cart Service (HTTP - `POST cart checkout`)
    
   → emit `OrderCreationRequested`

2) Order Service consumes
- Check cart not already processed
- Save Order(`VALIDATING`)

  → emit `ReserveInventory`

3) Catalog Service consumes
- Checks availability and updates stock

  → emit `InventoryReservationResponse`

4) Order Service consumes
- If success: update Order(`PENDING_PAYMENT`)  
- If invalid: update Order(`INVALID`) 
- If fail: update Order(`FAILED`)  
  → emit `OrderCreationResponse`

5) Cart Service consumes
- Update cart/order status

6) Frontend Cart Update
  - Poll `/cart/{cartId}`
  - If status = `PENDING_PAYMENT` → redirect to `/checkout/{orderId}` for payment
  - If status = `INVALID` → redirect to `/cart` and point out invalid items

See [FRONTEND.md](FRONTEND.md) for full frontend details.
