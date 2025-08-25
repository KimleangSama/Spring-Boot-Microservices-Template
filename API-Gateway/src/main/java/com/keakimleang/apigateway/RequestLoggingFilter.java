package com.keakimleang.apigateway;

import io.micrometer.context.ContextRegistry;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Component
@Slf4j
public class RequestLoggingFilter implements WebFilter {

    private static final String HEADER_TRACE_ID = "X-TraceId";
    private static final String TRACE_ID = "traceId";

    private static final List<String> SHOULD_NOT_FILTER_PATHS = List.of(
            "/api/v1/actuator/",  // changed to trailing slash to match path segments better
            "/actuator"
    );

    private final List<String> keysToMask = List.of("password");

    @NonNull
    @Override
    public Mono<Void> filter(final ServerWebExchange exchange,
                             final @NonNull WebFilterChain chain) {

        // Register MDC accessor for reactor context propagation (Micrometer context)
        ContextRegistry.getInstance().registerThreadLocalAccessor(
                TRACE_ID,
                () -> MDC.get(TRACE_ID),
                value -> MDC.put(TRACE_ID, value),
                () -> MDC.remove(TRACE_ID));

        // Generate new traceId and put into MDC
        final var traceId = UUID.randomUUID().toString();
        MDC.put(TRACE_ID, traceId);

        // Add traceId to request headers (mutate request)
        var mutatedRequest = exchange.getRequest().mutate()
                .header(HEADER_TRACE_ID, traceId)
                .build();

        // Add traceId header to response
        addTraceIdHeader(exchange.getResponse(), traceId);

        // Mutate exchange with new request containing traceId header
        var mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        // Skip logging for actuator paths
        final var path = mutatedRequest.getPath().value();
        if (SHOULD_NOT_FILTER_PATHS.stream().anyMatch(path::startsWith)) {
            return chain.filter(mutatedExchange)
                    .contextWrite(Context.of(TRACE_ID, traceId));
        }

        // Start time for request timing
        final var start = Instant.now();
        final var method = mutatedRequest.getMethod();
        final var response = mutatedExchange.getResponse();
        final var clientIp = getClientIpAddress(mutatedRequest);
        final var isMultipart = MediaType.MULTIPART_FORM_DATA.isCompatibleWith(mutatedRequest.getHeaders().getContentType());

        if (isMultipart) {
            // If multipart, skip logging request body (it can be large/binary)
            return chain.filter(mutatedExchange)
                    .doOnTerminate(() -> {
                        var end = Instant.now();
                        var timeTaken = Duration.between(start, end);
                        var status = response.getStatusCode();

                        log.info("traceId={}, clientIP={}, method={}, URI={}, httpCode={}, requestBody={}, spendTime={} ms",
                                traceId, clientIp, method, path, status, "", timeTaken.toMillis());
                    })
                    .contextWrite(Context.of(TRACE_ID, traceId));
        }

        // For non-multipart: capture and mask request body
        return mutatedRequest.getBody()
                .collectList()
                .flatMap(dataBuffers -> {
                    // Decode body buffers to string
                    var body = dataBuffers.stream()
                            .map(this::dataBufferToString)
                            .reduce("", String::concat);

                    // Mask sensitive keys
                    var regex = "(?<=(" + String.join("|", keysToMask) + ")=)[^&]+";
                    var maskedBody = body.replaceAll(regex, "******");

                    // Rebuild request body to allow downstream consumption
                    var bufferFactory = mutatedExchange.getResponse().bufferFactory();
                    var decoratedRequest = new ServerHttpRequestDecorator(mutatedRequest) {
                        @NonNull
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return Flux.just(bufferFactory.wrap(body.getBytes(StandardCharsets.UTF_8)));
                        }
                    };

                    var decoratedExchange = mutatedExchange.mutate()
                            .request(decoratedRequest)
                            .build();

                    return chain.filter(decoratedExchange)
                            .doOnTerminate(() -> {
                                var end = Instant.now();
                                var timeTaken = Duration.between(start, end);
                                var status = response.getStatusCode();

                                log.info("traceId={}, clientIP={}, method={}, URI={}, httpCode={}, requestBody={}, spendTime={} ms",
                                        traceId,
                                        clientIp,
                                        method,
                                        path,
                                        status,
                                        maskedBody.replaceAll("\\s+", ""),
                                        timeTaken.toMillis());
                            })
                            .contextWrite(Context.of(TRACE_ID, traceId));
                });
    }

    private String dataBufferToString(DataBuffer buffer) {
        byte[] bytes = new byte[buffer.readableByteCount()];
        buffer.read(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private void addTraceIdHeader(ServerHttpResponse response, String traceId) {
        response.getHeaders().add(HEADER_TRACE_ID, traceId);
    }

    private String getClientIpAddress(ServerHttpRequest request) {
        var ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            var remoteAddress = request.getRemoteAddress();
            ip = remoteAddress != null ? remoteAddress.getAddress().getHostAddress() : "Unknown";
        }
        return ip;
    }
}