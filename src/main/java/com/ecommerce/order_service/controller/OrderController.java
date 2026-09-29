package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.service.CurrencyServiceWrapper;
import com.ecommerce.order_service.service.InventoryServiceWrapper;
import com.ecommerce.order_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final InventoryServiceWrapper inventoryServiceWrapper;
    private final CurrencyServiceWrapper currencyServiceWrapper;
    private final OrderService orderService;

    // Inject wrappers instead of direct client interfaces
    public OrderController(InventoryServiceWrapper inventoryServiceWrapper,
                           CurrencyServiceWrapper currencyServiceWrapper, OrderService orderService) {
        this.inventoryServiceWrapper = inventoryServiceWrapper;
        this.currencyServiceWrapper = currencyServiceWrapper;
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<String> fetchOrderDetails(@RequestParam String orderNumber) {
        String result = orderService.getOrderDetails(orderNumber);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<String> fetchAllOrdersWithItems(@RequestParam(required = false) String itemName) {
        var orders = orderService.getAllOrdersWithItems(itemName);
        return ResponseEntity.ok("Fetched " + orders.size() + " orders with items containing: " + itemName);
    }

    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> createOrder(@Valid @RequestBody OrderRequest orderRequest,
                                                           @RequestParam(defaultValue = "USD") String currency) {
        Map<String, Object> response = orderService.placeOrder(orderRequest, currency);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}