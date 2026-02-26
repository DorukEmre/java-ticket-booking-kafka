namespace CatalogService.Events;

public static class Topics
{
    // order-service → catalog-service
    public const string RESERVE_INVENTORY = "reserve-inventory";
    public const string RELEASE_INVENTORY = "release-inventory";

    // catalog-service → order-service
    public const string INVENTORY_RESERVATION_FAILED = "inventory-reservation-failed";
    public const string INVENTORY_RESERVATION_INVALID = "inventory-reservation-invalid";
    public const string INVENTORY_RESERVATION_SUCCEEDED = "inventory-reservation-succeeded";
}
