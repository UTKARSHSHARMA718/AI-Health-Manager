package com.fitness.user_service.controllers;

import com.fitness.user_service.dtos.APIResponse;
import com.fitness.user_service.dtos.user.UserResponse;
import com.fitness.user_service.dtos.user.RegisterRequest;
import com.fitness.user_service.dtos.user.UserDto;
import com.fitness.user_service.services.UserServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(
        name = "User Management",
        description = "APIs for user registration and user retrieval."
)
public class UserController {

    private final UserServices userServices;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account using the supplied registration information."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or user already exists",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<APIResponse<UserDto>> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        UserDto user = userServices.registerUser(request);

        APIResponse<UserDto> response = APIResponse.<UserDto>builder()
                .success(true)
                .message("User has been registered!")
                .body(user)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get user by email",
            description = "Returns the user details for the specified email address."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User found successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    @GetMapping("/{email}")
    public ResponseEntity<APIResponse<UserDto>> getUserByEmail(
            @Parameter(
                    description = "Email address of the user",
                    example = "john.doe@example.com",
                    required = true
            )
            @PathVariable String email
    ) {

        UserDto userDto = userServices.getUserByEmail(email);

        APIResponse<UserDto> response = APIResponse.<UserDto>builder()
                .success(true)
                .message("User has been found!")
                .body(userDto)
                .build();

        return ResponseEntity.ok(response);
    }
}