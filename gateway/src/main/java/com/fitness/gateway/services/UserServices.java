package com.fitness.gateway.services;

import com.fitness.gateway.dtos.APIResponse;
import com.fitness.gateway.dtos.RegisterRequest;
import com.fitness.gateway.dtos.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServices {
    private final WebClient webClient;

    public Mono<APIResponse<UserDto>> getUserByEmail(String email) {
        return webClient.get()
                .uri("/api/users/{email}", email)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<APIResponse<UserDto>>() {})
                .onErrorResume(WebClientResponseException.BadRequest.class, ex -> {
                    return Mono.empty();
                });
    }

    public Mono<APIResponse<UserDto>> createUser(RegisterRequest request){
        return webClient.post().uri("/api/users")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<APIResponse<UserDto>>() {});
    }
}
