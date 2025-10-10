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
        OrderCreationRequested[🟣 order-requested]
        OrderCancelledRequested[🟣 order-cancelled]
        ReserveInventory[🟢 reserve-inventory]
        InventoryReleaseRequested[🟢 release-inventory]
        InventoryReservationFailed[🟣 inventory-reservation-failed]
        InventoryReservationInvalid[🟣 inventory-reservation-invalid]
        InventoryReservationSucceeded[🟣 inventory-reservation-succeeded]
        OrderFailed[🟣 order-failed]
        OrderInvalid[🟣 order-invalid]
        OrderSucceeded[🟣 order-succeeded]
    end

    FE -->|CheckoutCart (HTTP)| CartService
    CartService -->|🟣 OrderCreationRequested| OrderCreationRequested
    OrderCreationRequested --> OrderService

    OrderService -->|🟢 reserve-inventory| ReserveInventory
    ReserveInventory --> CatalogService

    CatalogService -->|🟣 inventory-reservation-failed| InventoryReservationFailed
    CatalogService -->|🟣 inventory-reservation-invalid| InventoryReservationInvalid
    CatalogService -->|🟣 inventory-reservation-succeeded| InventoryReservationSucceeded

    InventoryReservationFailed --> OrderService
    InventoryReservationInvalid --> OrderService
    InventoryReservationSucceeded --> OrderService

    OrderService -->|🟣 order-succeeded| OrderSucceeded
    OrderService -->|🟣 order-failed| OrderFailed
    OrderService -->|🟣 order-invalid| OrderInvalid

    OrderSucceeded --> CartService
    OrderFailed --> CartService
    OrderInvalid --> CartService

    CartService -->|Return status (poll/push)| FE
