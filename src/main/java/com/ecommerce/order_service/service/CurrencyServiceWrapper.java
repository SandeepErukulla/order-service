package com.ecommerce.order_service.service;

import com.ecommerce.order_service.client.CurrencyClient;
import com.ecommerce.order_service.dto.CurrencyResponse;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CurrencyServiceWrapper {

    private final CurrencyClient currencyClient;

    public CurrencyServiceWrapper(CurrencyClient currencyClient) {
        this.currencyClient = currencyClient;
    }

    @RateLimiter(name = "currencyService", fallbackMethod = "getExchangeRatesFallback")
    public CurrencyResponse getExchangeRates(String baseCurrency) {
        return currencyClient.getExchangeRates(baseCurrency);
    }

    // Fallback must match method signature + Throwable
    public CurrencyResponse getExchangeRatesFallback(String baseCurrency, Throwable throwable) {
        System.err.println("Rate limit reached for currency provider: " + throwable.getMessage());

        CurrencyResponse fallbackResponse = new CurrencyResponse();
        fallbackResponse.setResult("RATE_LIMIT_EXCEEDED");
        fallbackResponse.setBase_code(baseCurrency);
        // Set default rates map if your DTO has a rates field
        fallbackResponse.setConversion_rates(Map.of("USD", 1.0, "EUR", 0.92, "INR", 83.0));

        return fallbackResponse;
    }
}