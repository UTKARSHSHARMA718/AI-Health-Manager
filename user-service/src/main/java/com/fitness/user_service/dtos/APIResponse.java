package com.fitness.user_service.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Standard API response wrapper")
public class APIResponse<T> {

    @Schema(example = "true")
    private boolean success;

    @Schema(example = "User has been registered!")
    private String message;

    private T body;
}
