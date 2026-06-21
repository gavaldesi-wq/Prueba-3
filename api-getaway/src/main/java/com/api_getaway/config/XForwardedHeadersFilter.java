package com.api_getaway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class XForwardedHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Crear una copia mutada del request con los headers X-Forwarded-*
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-Forwarded-For", getClientIp(exchange))
                        .header("X-Forwarded-Proto", "http")
                        .header("X-Forwarded-Host", "localhost")
                        .header("X-Forwarded-Port", "9090")
                        
                        .build())
                .build();

        return chain.filter(mutatedExchange);
    }

    private String getClientIp(ServerWebExchange exchange) {
        String clientIp = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = exchange.getRequest().getRemoteAddress() != null 
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() 
                : "127.0.0.1";
        }
        return clientIp;
    }

    @Override
    public int getOrder() {
        // Ejecutar con alta prioridad (antes que otros filtros)
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
