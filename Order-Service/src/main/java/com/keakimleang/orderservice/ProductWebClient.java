package com.keakimleang.orderservice;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductWebClient {
    private final WebClientConfig webClientConfig;

    public Mono<List<ProductResponse>> getAllProducts() {
        return webClientConfig.productWebClient().get()
                .uri(uriBuilder -> uriBuilder.path("/api/product")
                        .build())
                .retrieve()
                .bodyToFlux(ProductResponse.class)
                .collectList();
    }
}
