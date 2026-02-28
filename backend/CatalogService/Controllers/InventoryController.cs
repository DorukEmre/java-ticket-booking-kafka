using Microsoft.AspNetCore.Mvc;
// using Swashbuckle.AspNetCore.Annotations;

using CatalogService.Models;
using CatalogService.Services;


namespace CatalogService.Controllers;

[ApiController]
[Route("api/v1/catalog")]
public class InventoryController(InventoryService inventoryService)
    : ControllerBase
{
    private readonly InventoryService _inventoryService = inventoryService;


    [HttpPost("validate-cart")]
    // [SwaggerOperation(Hidden = true, Description = "Internal: used by cart service to validate carts. Hidden from public API docs.")]
    public ActionResult<Dictionary<long, bool>> ValidateCart([FromBody] Cart cart)
    {
        Console.WriteLine("POST /api/v1/catalog/validate-cart called");

        var result = _inventoryService.ValidateCart(cart);

        return Ok(result);
    }
}
