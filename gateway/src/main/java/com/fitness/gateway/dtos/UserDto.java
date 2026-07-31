package com.fitness.gateway.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private String keyCloakId;
    private String email;

    private String name;

    private String password;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
