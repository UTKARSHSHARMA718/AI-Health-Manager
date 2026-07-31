package com.fitness.activity_service.dtos;

import com.fitness.activity_service.models.ActivityType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class CreateActivityRequest {
    @NotNull
    private String userId;

    @NotNull
    private ActivityType activity;

    @NotNull
    @Min(0)
    private Integer durationInSeconds;

    @NotNull
    @Min(0)
    private Integer caloriesBurned;

    @NotNull
    private LocalDateTime startTime;

    private Map<String, Object> additionalMetrics;
}
