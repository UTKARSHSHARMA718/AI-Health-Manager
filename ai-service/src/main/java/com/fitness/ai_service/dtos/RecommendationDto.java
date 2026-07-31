package com.fitness.ai_service.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(
        name = "Recommendation",
        description = "Represents an AI-generated fitness recommendation."
)
public class RecommendationDto {
    private String id;
    private String activityId;
    private String userId;
    private String recommendation;
    private List<String> safety;
    private List<String> improvements;
    private List<String> suggestions;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
