package com.ecommerce.order_service.service;

import com.ecommerce.order_service.client.InventoryClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceWrapper {

    private final InventoryClient inventoryClient;

    public InventoryServiceWrapper(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "isInStockFallback")
    public boolean checkInventory(String skuCode, Integer quantity) {
        return inventoryClient.checkStock(skuCode, quantity);
    }

    // Fallback signature must match target method arguments plus Throwable
    public boolean isInStockFallback(String skuCode, Integer quantity, Throwable throwable) {
        System.err.println("Inventory Service down/unreachable. Fallback triggered: " + throwable.getMessage());
        return false;
    }
}