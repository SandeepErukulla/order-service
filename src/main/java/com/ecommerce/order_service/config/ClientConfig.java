package com.ecommerce.order_service.config;

import com.ecommerce.order_service.client.CurrencyClient;
import com.ecommerce.order_service.client.InventoryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ClientConfig {

    // 1. Primary standard builder used by Spring Cloud / Eureka internal calls
    @Bean
    @Primary
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    // 2. Load-Balanced Builder for internal microservice communication
    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    // 3. Internal Microservice Client (Uses @LoadBalanced builder)
    @Bean
    public InventoryClient inventoryClient(@LoadBalanced RestClient.Builder loadBalancedRestClientBuilder) {
        RestClient restClient = loadBalancedRestClientBuilder
                .baseUrl("http://inventory-service")
                .build();

        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(InventoryClient.class);
    }

    // 4. External Third-Party API Client (Uses standard primary builder)
    @Bean
    public CurrencyClient currencyClient(RestClient.Builder restClientBuilder) {
        RestClient restClient = restClientBuilder
                .baseUrl("https://open.er-api.com")
                .build();

        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(CurrencyClient.class);
    }
}