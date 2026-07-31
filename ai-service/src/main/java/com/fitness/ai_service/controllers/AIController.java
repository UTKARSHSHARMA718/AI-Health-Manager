package com.fitness.ai_service.controllers;

import com.fitness.ai_service.dtos.APIResponse;
import com.fitness.ai_service.dtos.RecommendationDto;
import com.fitness.ai_service.dtos.RecommendationListResponse;
import com.fitness.ai_service.services.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(
        name = "AI Recommendations",
        description = "APIs for retrieving AI-generated fitness recommendations for users."
)
public class AIController {

    private final AIService aiService;

    @Operation(
            summary = "Get AI recommendations for a user",
            description = "Retrieves a list of AI-generated fitness recommendations based on the user's activity history."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Recommendations retrieved successfully",
                    content = @Content(
                            schema = @Schema(implementation = RecommendationListResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User or recommendations not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    @GetMapping("/{userId}")
    public ResponseEntity<APIResponse<List<RecommendationDto>>> getRecommendationByUserId(

            @Parameter(
                    description = "Unique identifier of the user",
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true
            )
            @PathVariable String userId
    ) {
        List<RecommendationDto> list = aiService.getRecommendationByUserId(userId);
        APIResponse<List<RecommendationDto>> response = APIResponse.<List<RecommendationDto>>builder()
                .success(true)
                .message("Recommendations retrieved successfully!")
                .body(list)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{activityId}")
    public void reGenerateRecommendationByActivityId(@PathVariable String activityId){
        aiService.
    }
}