package com.fitness.activity_service.dtos;

import com.fitness.activity_service.models.ActivityType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class ActivityDto {
    private String id;

    private String userId;
    private ActivityType activity;
    private Integer durationInSeconds;
    private Integer caloriesBurned;
    private LocalDateTime startTime;

    private Map<String, Object> additionalMetrics;

    private LocalDateTime createdAt;

    protected LocalDateTime updatedAt;
}
