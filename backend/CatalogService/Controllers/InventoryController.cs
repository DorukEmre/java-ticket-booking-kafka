using Microsoft.AspNetCore.Mvc;
// using Swashbuckle.AspNetCore.Annotations;
using CatalogService.Models;
using CatalogService.Services;
using System.Collections.Generic;

namespace CatalogService.Controllers;

[ApiController]
[Route("api/v1/catalog")]
public class InventoryController : ControllerBase
{
    private readonly InventoryService _inventoryService;

    public InventoryController(InventoryService inventoryService)
    {
        _inventoryService = inventoryService;
    }

    [HttpPost("validate-cart")]
    // [SwaggerOperation(Hidden = true, Description = "Internal: used by cart service to validate carts. Hidden from public API docs.")]
    public ActionResult<Dictionary<long, bool>> ValidateCart([FromBody] Cart cart)
    {
        Console.WriteLine("POST /api/v1/catalog/validate-cart called");

        var result = _inventoryService.ValidateCart(cart);

        return Ok(result);
    }

}
