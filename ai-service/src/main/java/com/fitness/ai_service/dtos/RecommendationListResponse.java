package com.fitness.ai_service.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "RecommendationListResponse")
public class RecommendationListResponse extends APIResponse<List<RecommendationDto>> {
}
