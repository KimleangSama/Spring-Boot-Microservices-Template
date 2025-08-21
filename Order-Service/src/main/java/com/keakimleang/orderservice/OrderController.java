package com.keakimleang.orderservice;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final ProductWebClient productWebClient;
    private final InventoryWebClient inventoryWebClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<List<ProductResponse>> placeOrder(@RequestBody OrderRequest orderRequest) {
        return productWebClient.getAllProducts().flatMap(products -> {
            log.info("Product data: {}", products);
            return inventoryWebClient.isInStock(orderRequest.skuCode(), orderRequest.quantity())
                    .flatMap(isProductInStock -> {
                        if (isProductInStock) {
                            return Mono.just("Order Placed Successfully");
                        } else {
                            return Mono.just("Product with SkuCode " + orderRequest.skuCode() + " is not in stock");
                        }
                    })
                    .thenReturn(products);
        });
    }
}
