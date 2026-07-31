package com.fitness.gateway.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegisterRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank
    private String keyCloakId;

    @Email
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank
    @Size(min = 8, max = 12)
    private String password;
}
