package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.dto.OrderRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @GetMapping
    public ResponseEntity<String> createOrder() {
        // Implementation for creating an order
        return ResponseEntity.ok("Order created successfully");
    }

    @PostMapping("/submit")
    public ResponseEntity<Map<String,Object>> createOrder(@Valid @RequestBody OrderRequest orderRequest) {

        String OrderId = UUID.randomUUID().toString();

        Map<String, Object> response = Map.of(
                "orderId", OrderId,
                "status", "CREATED",
                "productId", orderRequest.getProductId(),
                "quantity", orderRequest.getQuantity(),
                "customerId", orderRequest.getCustomerId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
}
