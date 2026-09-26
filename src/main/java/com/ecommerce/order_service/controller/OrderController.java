package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.client.CurrencyClient;
import com.ecommerce.order_service.client.InventoryClient;
import com.ecommerce.order_service.dto.CurrencyResponse;
import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.exception.InvalidOrderException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final InventoryClient inventoryClient;
    private final CurrencyClient currencyClient;

    public OrderController(InventoryClient inventoryClient, CurrencyClient currencyClient) {
        this.inventoryClient = inventoryClient;
        this.currencyClient = currencyClient;
    }

    @GetMapping
    public ResponseEntity<String> createOrder() {
        // Implementation for creating an order
        return ResponseEntity.ok("Order created successfully");
    }

    @PostMapping("/submit")
    public ResponseEntity<Map<String,Object>> createOrder(@Valid @RequestBody OrderRequest orderRequest,
                                                          @RequestParam(defaultValue = "USD") String currency) {

        boolean inStock = inventoryClient
                .checkStock(orderRequest.getProductId(), orderRequest.getQuantity());
        if(!inStock) {
            throw new InvalidOrderException("Product with ID " + orderRequest.getProductId() + " is out of stock.");
        }

        double basePriceUsd = 150.0;
        CurrencyResponse currencyResponse = currencyClient.getExchangeRates("USD");
        Double conversionRate = (currencyResponse != null && currencyResponse.getConversion_rates() != null)
                ? currencyResponse.getConversion_rates().getOrDefault(currency.toUpperCase(), 1.0)
                : 1.0;

        double localizedTotal = basePriceUsd * orderRequest.getQuantity() * conversionRate;

        String orderId = UUID.randomUUID().toString();

        Map<String, Object> response = Map.of(
                "orderId", orderId,
                "status", "CREATED",
                "productId", orderRequest.getProductId(),
                "quantity", orderRequest.getQuantity(),
                "customerId", orderRequest.getCustomerId(),
                "totalPrice", localizedTotal,
                "currency", currency.toUpperCase()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
}
