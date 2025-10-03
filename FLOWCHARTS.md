flowchart LR
    subgraph FE[Frontend (React)]
    end

    subgraph CartService[Cart Service]
        CartRedis[(Redis - Cart Cache)]
    end

    subgraph OrderService[Order Service]
        OrderDB[(MySQL - Orders, Customers)]
    end

    subgraph CatalogService[Catalog Service]
        CatalogDB[(MySQL - Events, Venues)]
    end

    subgraph Kafka[Kafka Topics]
        OrderRequested[🟣 order-requested]
        ReserveInventory[🟢 reserve-inventory]
        InventoryReserved[🟣 inventory-reserved]
        InventoryReservationFailed[🟣 inventory-reservation-failed]
        OrderCreated[🟣 order-created]
        OrderFailed[🟣 order-failed]
    end

    FE -->|CheckoutCart (HTTP)| CartService
    CartService -->|🟣 OrderRequested| OrderRequested
    OrderRequested --> OrderService

    OrderService -->|🟢 ReserveInventory| ReserveInventory
    ReserveInventory --> CatalogService

    CatalogService -->|🟣 InventoryReserved| InventoryReserved
    CatalogService -->|🟣 InventoryReservationFailed| InventoryReservationFailed

    InventoryReserved --> OrderService
    InventoryReservationFailed --> OrderService

    OrderService -->|🟣 OrderCreated| OrderCreated
    OrderService -->|🟣 OrderFailed| OrderFailed

    OrderCreated --> CartService
    OrderFailed --> CartService

    CartService -->|Return status (poll/push)| FE
