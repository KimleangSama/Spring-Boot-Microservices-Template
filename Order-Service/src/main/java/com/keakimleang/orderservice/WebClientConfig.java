package com.keakimleang.orderservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${services.uris.product-service}")
    private String productServiceUri;
    @Value("${services.uris.inventory-service}")
    private String inventoryServiceUri;

    public WebClient productWebClient() {
        return WebClient.builder()
                .baseUrl(productServiceUri)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .defaultHeader("User-Agent", "Order-Service-WebClient")
                .build();
    }

    public WebClient inventoryWebClient() {
        return WebClient.builder()
                .baseUrl(inventoryServiceUri)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .defaultHeader("User-Agent", "Order-Service-WebClient")
                .build();
    }
}
