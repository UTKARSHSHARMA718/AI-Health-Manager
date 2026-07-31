package com.fitness.activity_service.Services;

import com.fitness.activity_service.dtos.User;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Builder
@Data
@RequiredArgsConstructor
public class UserService {
    private final WebClient userServiceWebClient;

    public User getUserById(UUID userId){
        try{
//            search for better pattern for handling api call response
            return userServiceWebClient.get().uri("/api/users/{userId}", userId)
                    .retrieve().bodyToMono(User.class).block();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
