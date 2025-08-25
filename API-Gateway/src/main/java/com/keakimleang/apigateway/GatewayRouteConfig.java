package com.keakimleang.apigateway;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class GatewayRouteConfig {
    @Value("${services.uris.product-service}")
    private String productServiceUri;
    @Value("${services.uris.order-service}")
    private String orderServiceUri;
    @Value("${services.uris.inventory-service}")
    private String inventoryServiceUri;

    private final CustomGlobalFilter customGlobalFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service", r -> r.path("/products/**")
                        .filters(corsFilter("products"))
                        .uri(productServiceUri))
                .route("order-service", r -> r.path("/orders/**")
                        .filters(corsFilter("orders"))
                        .uri(orderServiceUri))
                .route("inventory-service", r -> r.path("/inventories/**")
                        .filters(corsFilter("inventories"))
                        .uri(inventoryServiceUri))
                .build();
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        // Configure RedisRateLimiter with a limit of 5 requests per second and a burst capacity of 10
        return new RedisRateLimiter(5, 10);
    }

    @Bean
    public KeyResolver remoteAddressKeyResolver() {
        return exchange -> Mono.just(
                Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("X-Forwarded-For"))
                        .orElseGet(() -> Objects.requireNonNull(exchange.getRequest().getRemoteAddress())
                                .getAddress().getHostAddress())
        );
    }

    private Function<GatewayFilterSpec, UriSpec> corsFilter(final String service) {
        return f -> f
                .filter(customGlobalFilter)
                .requestRateLimiter(
                        config -> config
                                .setRateLimiter(redisRateLimiter())
                                .setKeyResolver(remoteAddressKeyResolver())
                )
                .retry(config -> config
                        .setRetries(3)
                        .setStatuses(HttpStatus.BAD_GATEWAY, HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.SERVICE_UNAVAILABLE)
                        .setMethods(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT)
                        .setBackoff(Duration.ofMillis(50), Duration.ofMillis(500), 2, true)
                )
                .circuitBreaker(circuitBreakerConfig -> circuitBreakerConfig
                        .setName(service)
                        .setFallbackUri("forward:/fallback") // Redirect to this URI on circuit open or timeout
                )
                .rewritePath("/" + service + "/(?<segment>.*)", "/${segment}")
                .setResponseHeader("Access-Control-Allow-Origin", "*")
                .setResponseHeader("Access-Control-Allow-Methods", "*")
                .setResponseHeader("Access-Control-Expose-Headers", "*");
    }
}
