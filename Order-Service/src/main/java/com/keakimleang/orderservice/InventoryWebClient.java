package com.keakimleang.orderservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryWebClient {
    private final WebClientConfig webClientConfig;

    public Mono<Boolean> isInStock(String skuCode, Integer quantity) {
        return webClientConfig.inventoryWebClient().get()
                .uri(uriBuilder -> uriBuilder.path("/api/inventory")
                        .queryParam("skuCode", skuCode)
                        .queryParam("quantity", quantity)
                        .build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnNext(inStock -> log.info("{} is in stock: {}", skuCode, inStock));
    }
}
