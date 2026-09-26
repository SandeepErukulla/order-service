package com.ecommerce.order_service.config;

import com.ecommerce.order_service.client.CurrencyClient;
import com.ecommerce.order_service.client.InventoryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ClientConfig {

    // 1. Define the LoadBalanced Builder Bean for Spring Cloud
    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    // 2. Internal Microservice Client (Uses LoadBalancer)
    @Bean
    public InventoryClient inventoryClient(RestClient.Builder loadBalancedRestClientBuilder) {
        RestClient restClient = loadBalancedRestClientBuilder
                .baseUrl("http://inventory-service")
                .build();

        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(InventoryClient.class);
    }

    // 3. External Third-Party API Client (NO LoadBalancer)
    @Bean
    public CurrencyClient currencyClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.er-api.com")
                .build();

        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(CurrencyClient.class);
    }
}