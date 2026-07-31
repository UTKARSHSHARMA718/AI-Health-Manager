package com.fitness.gateway.services;

import com.fitness.gateway.dtos.RegisterRequest;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.text.ParseException;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeyCloakFilterService implements WebFilter {
    private final UserServices userServices;

    public RegisterRequest getUserDetails(String token) throws ParseException {
        String tokenWithoutBearer = token.replace("Bearer", "").trim();
        SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        RegisterRequest request = RegisterRequest.builder()
                .email(claims.getStringClaim("email"))
                .name(claims.getStringClaim("name"))
                .keyCloakId(claims.getStringClaim("sub"))
                .password("test@123")
                .build();
        return request;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        try {
            log.info("Request processing...");

            if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
                log.info("OPTIONS request skipping checks....");
                return chain.filter(exchange);
            }

            String token = exchange.getRequest()
                    .getHeaders()
                    .getFirst("Authorization");

            if (token == null || token.isEmpty()) {
                log.warn("Missing Authorization header");
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete(); // Short-circuits request immediately
            }

            RegisterRequest registerRequest = getUserDetails(token);
            String email = registerRequest.getEmail();

            return userServices.getUserByEmail(email)
                    .flatMap(user -> {
                        // User exists

                        ServerHttpRequest request = exchange.getRequest()
                                .mutate()
                                .header("X-USER-ID", user.getBody().getKeyCloakId())
                                .build();

                        return chain.filter(
                                exchange.mutate()
                                        .request(request)
                                        .build()
                        );
                    })
                    .switchIfEmpty(
                            userServices.createUser(registerRequest)
                                    .flatMap(createdUser -> {

                                        ServerHttpRequest request = exchange.getRequest()
                                                .mutate()
                                                .header("X-USER-ID", createdUser.getBody().getKeyCloakId())
                                                .build();

                                        return chain.filter(
                                                exchange.mutate()
                                                        .request(request)
                                                        .build()
                                        );
                                    })
                    );

        } catch (ParseException e) {
            return Mono.error(e);
        }
    }
}
