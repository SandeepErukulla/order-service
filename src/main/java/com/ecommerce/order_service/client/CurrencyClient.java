package com.ecommerce.order_service.client;

import com.ecommerce.order_service.dto.CurrencyResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface CurrencyClient {

    @GetExchange("/v6/latest/{baseCurrency}")
    CurrencyResponse getExchangeRates(@PathVariable("baseCurrency") String baseCurrency);
}
