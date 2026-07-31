package com.fitness.user_service.dtos.user;

import com.fitness.user_service.models.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(
        name = "User",
        description = "Represents a registered user."
)
public class UserDto {

    @Schema(
            description = "Unique identifier of the user.",
            example = "550e8400-e29b-41d4-a716-446655440000",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private UUID id;

    @Schema(
            description = "Unique user identifier from Keycloak.",
            example = "b9f0b93f-56f0-40b8-aab9-6d3d98f4b1f4",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String keyCloakId;

    @Schema(
            description = "User's email address.",
            example = "john.doe@example.com"
    )
    private String email;

    @Schema(
            description = "Full name of the user.",
            example = "John Doe"
    )
    private String name;

    // Remove this field if UserDto is returned from your APIs.
    @Schema(
            description = "User password. This field should never be exposed in API responses.",
            accessMode = Schema.AccessMode.WRITE_ONLY,
            example = "Password@123"
    )
    private String password;

    @Schema(
            description = "Role assigned to the user.",
            example = "USER"
    )
    private Role role;

    @Schema(
            description = "Timestamp when the user account was created.",
            example = "2026-07-05T10:15:30",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime createdAt;

    @Schema(
            description = "Timestamp when the user account was last updated.",
            example = "2026-07-05T12:30:45",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime updatedAt;
}