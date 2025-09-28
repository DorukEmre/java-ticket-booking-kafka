package me.doruk.inventory.controller;

import me.doruk.inventory.response.EventInventoryResponse;
import me.doruk.inventory.response.VenueInventoryResponse;
import me.doruk.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(final InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/inventory/events")
    public @ResponseBody List<EventInventoryResponse> inventoryGetAllEvents() {
        return inventoryService.GetAllEvents();
    }

    @GetMapping("/inventory/venue/{venueId")
    public @ResponseBody VenueInventoryResponse inventoryByVenueId(@PathVariable("VenueId") Long venueId) {
        return inventoryService.getVenueInformation(venueId);
    }
}
