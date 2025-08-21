package com.keakimleang.apigateway;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.rewritePath;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class RouteConfig {
    @Value("${services.urls.product-service}")
    private String productServiceUrl;
    @Value("${services.urls.order-service}")
    private String orderServiceUrl;
    @Value("${services.urls.inventory-service}")
    private String inventoryServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> productServiceRoute() {
        return route("product_service")
                .route(RequestPredicates.path("/api/product"), http())
                .route(RequestPredicates.path("/product/actuator/health"), http())
                .before(rewritePath("/product/actuator/health", "/actuator/health"))
                .before(uri(productServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("productServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        return route("order_service")
                .route(RequestPredicates.path("/api/order"), http())
                .route(RequestPredicates.path("/order/actuator/health"), http())
                .before(rewritePath("/order/actuator/health", "/actuator/health"))
                .before(uri(orderServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("orderServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoute() {
        return route("inventory_service")
                .route(RequestPredicates.path("/api/inventory"), http())
                .route(RequestPredicates.path("/inventory/actuator/health"), http())
                .before(rewritePath("/inventory/actuator/health", "/actuator/health"))
                .before(uri(inventoryServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

//    @Bean
//    public RouterFunction<ServerResponse> productServiceSwaggerRoute() {
//        return route("product_service_swagger")
//                .route(RequestPredicates.path("/aggregate/product-service/v3/api-docs"), http())
//                .before(uri(productServiceUrl))
//                .filter(CircuitBreakerFilterFunctions.circuitBreaker("productServiceSwaggerCircuitBreaker",
//                        URI.create("forward:/fallbackRoute")))
//                .filter(setPath("/api-docs"))
//                .build();
//    }

    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        return route("fallbackRoute")
                .GET("/fallbackRoute", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Service Unavailable. Please try again later."))
                .build();
    }
}
