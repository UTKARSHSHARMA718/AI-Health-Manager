package com.fitness.ai_service.records;

import java.util.List;

public record OpenAIRequest(String model, List<OpenAIMessage> messages) {}