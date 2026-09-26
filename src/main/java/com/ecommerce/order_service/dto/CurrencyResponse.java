package com.ecommerce.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyResponse {
    private String result;
    private String base_code;
    private Map<String, Double> conversion_rates;
}
