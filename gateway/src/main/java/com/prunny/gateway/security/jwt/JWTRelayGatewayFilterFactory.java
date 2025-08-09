package com.prunny.gateway.security.jwt;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class JWTRelayGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private static final String BEARER = "Bearer ";


    private final WebClient webClient;

    public JWTRelayGatewayFilterFactory(WebClient.Builder webClientBuilder, @Value("${auth.service.url:http://localhost:4005}") String authServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(authServiceUrl).build();
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String bearerToken = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION);
            if (bearerToken == null || !bearerToken.startsWith(BEARER)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return webClient.get()
                .uri("/api/auth/validate")
                .header(AUTHORIZATION, bearerToken)
                .retrieve()
                .toBodilessEntity()
                .then(chain.filter(exchange));
        };
    }
}
