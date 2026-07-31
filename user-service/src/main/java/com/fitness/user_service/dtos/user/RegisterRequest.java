package com.fitness.user_service.dtos.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for registering a new user")
public class RegisterRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    @Schema(
            description = "Full name of the user",
            example = "John Doe"
    )
    private String name;

    @NotBlank
    @Schema(
            description = "User uuid in keycloak",
            example = "i12wj01j219eeuj38"
    )
    private String keyCloakId;

    @Email
    @NotBlank(message = "Email is required")
    @Schema(
            description = "User email address",
            example = "john@example.com"
    )
    private String email;

    @NotBlank
    @Size(min = 8, max = 12)
    @Schema(
            description = "Password (minimum 8 characters)",
            example = "Password@123"
    )
    private String password;
}
