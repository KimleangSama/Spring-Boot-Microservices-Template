package com.keakimleang.inventoryservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@RequestParam String skuCode, @RequestParam Integer quantity) {
        // Simulate checking inventory
        // In a real application, you would check the database or an external service
        // For any other SKU or if the quantity exceeds available stock
        // Not in stock
        log.info("Checking if inventory is in stock");
        if ("IPHONE".equalsIgnoreCase(skuCode) && quantity <= 100) {
            return true; // In stock
        } else return "SAMSUNG".equalsIgnoreCase(skuCode) && quantity <= 50; // In stock
    }
}
