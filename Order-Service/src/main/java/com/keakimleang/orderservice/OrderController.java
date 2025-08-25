package com.keakimleang.orderservice;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    private final OrderRepository orderRepository;
    private final RabbitProps rabbitProps;
    private final RabbitTemplate rabbitTemplate;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> placeOrder(@RequestBody OrderRequest orderRequest) {
        return productWebClient.getAllProducts().flatMap(products -> {
            log.info("Product data: {}", products);
            return inventoryWebClient.isInStock(orderRequest.skuCode(), orderRequest.quantity())
                    .flatMap(isProductInStock -> {
                        if (isProductInStock) {
                            Order order = new Order();
                            order.setOrderNumber(UUID.randomUUID().toString());
                            order.setPrice(orderRequest.price().multiply(BigDecimal.valueOf(orderRequest.quantity())));
                            order.setSkuCode(orderRequest.skuCode());
                            order.setQuantity(orderRequest.quantity());
                            return orderRepository.save(order)
                                    .doOnSuccess(ordered -> {
                                        log.info("Placed order: {}", ordered);
                                        rabbitTemplate.convertAndSend(rabbitProps.getExchangeName(), rabbitProps.getRoutingKey(), ordered.getOrderNumber());
                                    })
                                    .then(Mono.just("Order Placed Successfully"))
                                    .thenReturn("Order Placed Successfully");
                        } else {
                            return Mono.just("Product with SkuCode " + orderRequest.skuCode() + " is not in stock");
                        }
                    });
        });
    }
}
