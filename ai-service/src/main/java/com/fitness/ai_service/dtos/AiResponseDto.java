package com.fitness.ai_service.dtos;

import lombok.Data;

import java.util.List;

@Data
public class AiResponseDto {
    private String recommendation;
    private List<String> safety;
    private List<String> improvements;
    private List<String> suggestions;
}
