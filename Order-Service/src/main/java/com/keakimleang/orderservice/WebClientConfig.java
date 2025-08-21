package com.keakimleang.orderservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${services.urls.product-service}")
    private String productServiceUrl;
    @Value("${services.urls.inventory-service}")
    private String inventoryServiceUrl;

    public WebClient productWebClient() {
        return WebClient.builder()
                .baseUrl(productServiceUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .defaultHeader("User-Agent", "Order-Service-WebClient")
                .build();
    }

    public WebClient inventoryWebClient() {
        return WebClient.builder()
                .baseUrl(inventoryServiceUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .defaultHeader("User-Agent", "Order-Service-WebClient")
                .build();
    }
}
