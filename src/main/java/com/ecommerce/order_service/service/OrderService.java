package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dto.CurrencyResponse;
import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.entity.OrderItem;
import com.ecommerce.order_service.exception.InvalidOrderException;
import com.ecommerce.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryServiceWrapper inventoryServiceWrapper;
    private final CurrencyServiceWrapper currencyServiceWrapper;

    public OrderService(OrderRepository orderRepository,
                        InventoryServiceWrapper inventoryServiceWrapper,
                        CurrencyServiceWrapper currencyServiceWrapper) {
        this.orderRepository = orderRepository;
        this.inventoryServiceWrapper = inventoryServiceWrapper;
        this.currencyServiceWrapper = currencyServiceWrapper;
    }

    @Transactional
    public Map<String, Object> placeOrder(OrderRequest orderRequest, String currency) {
        // 1. Circuit Breaker Check
        boolean inStock = inventoryServiceWrapper.checkInventory(orderRequest.getProductId(), orderRequest.getQuantity());
        if (!inStock) {
            throw new InvalidOrderException("Product with ID " + orderRequest.getProductId() + " is out of stock or inventory service is unreachable.");
        }

        // 2. Rate Limiter Check
        double basePriceUsd = 150.0;
        CurrencyResponse currencyResponse = currencyServiceWrapper.getExchangeRates("USD");
        Double conversionRate = (currencyResponse != null && currencyResponse.getConversion_rates() != null)
                ? currencyResponse.getConversion_rates().getOrDefault(currency.toUpperCase(), 1.0)
                : 1.0;

        double localizedTotal = basePriceUsd * orderRequest.getQuantity() * conversionRate;
        String generatedOrderNumber = UUID.randomUUID().toString();

        // 3. Persist Order and OrderItem in DB
        Order order = new Order();
        order.setOrderNumber(generatedOrderNumber);

        OrderItem item = new OrderItem();
        item.setSkuCode(orderRequest.getProductId());
        item.setQuantity(orderRequest.getQuantity());
        item.setPrice(BigDecimal.valueOf(localizedTotal));

        // Convenience method linking bidirectional relationship
        order.addOrderItem(item);

        orderRepository.save(order);

        return Map.of(
                "orderNumber", generatedOrderNumber,
                "status", "CREATED",
                "productId", orderRequest.getProductId(),
                "quantity", orderRequest.getQuantity(),
                "customerId", orderRequest.getCustomerId(),
                "totalPrice", localizedTotal,
                "currency", currency.toUpperCase()
        );
    }

    @Transactional(readOnly = true)
    public String getOrderDetails(String orderNumber) {
        return orderRepository.findByOrderNumberWithItems(orderNumber)
                .map(order -> "Order Number: " + order.getOrderNumber() + ", Items Count: " + order.getItems().size())
                .orElse("Order not found for number: " + orderNumber);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrdersWithItems(String itemName) {
        return orderRepository.findAllWithItems(itemName);
    }
}