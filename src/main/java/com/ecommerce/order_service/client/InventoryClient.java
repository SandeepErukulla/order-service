package com.ecommerce.order_service.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/api/v1/inventory")
public interface InventoryClient {
    @GetExchange("/check/{productId}")
    boolean checkStock(
            @PathVariable("productId") String productId,
            @RequestParam("quantity") Integer quantity
    );
}
