package com.fitness.ai_service.services;

import com.fitness.ai_service.dtos.ActivityDto;
import com.fitness.ai_service.dtos.AiResponseDto;
import com.fitness.ai_service.models.Recommendation;
import com.fitness.ai_service.records.OpenAIMessage;
import com.fitness.ai_service.records.OpenAIRequest;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAIService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper; // we use this whenever we want to access anything from a JSON string

    @Value("${open-ai.url}")
    private String url;

    @Value("${open-ai.api-key}")
    private String apiKey;

    @Value("${open-ai.model}")
    private String model;

    public String getActivityPrompt(){
        return """
                You are an expert AI fitness and health coach. 
                Your task is to analyze a user's logged physical activity and provide highly personalized, actionable insights.
                
                You must return your response strictly as a single JSON object. 
                Do not include any conversational filler, markdown formatting (like ```json), or text outside the JSON structure.
                
                The JSON structure must exactly match this blueprint:
                {
                  "recommendation": "A concise, motivating 2-3 sentence overall summary of their performance and what it means for their fitness goals.",
                  "safety": [
                    "Specific safety tip 1 based on the activity type and duration.",
                    "Specific safety tip 2 regarding recovery, hydration, or injury prevention."
                  ],
                  "improvements": [
                    "Direct feedback on how to optimize this specific workout next time.",
                    "Technique or pacing advice to get better results."
                  ],
                  "suggestions": [
                    "Alternative or progression workouts they can try next.",
                    "A cross-training suggestion that pairs well with this activity."
                  ]
                }
                """;
    }

    public String getUserPrompt(ActivityDto activityDto){
        // Serialize or safely map additional metrics to string
        String metricsText = (activityDto.getAdditionalMetrics() != null)
                ? activityDto.getAdditionalMetrics().toString()
                : "None provided";

        return """
                Please analyze the following user activity data and generate the JSON response:
                
                - Activity Type: %s
                - Duration: %d seconds (approx. %.1f minutes)
                - Calories Burned: %d kcal
                - Start Time: %s
                - Additional Metrics: %s
                
                Ensure the fields 'safety', 'improvements', and 'suggestions' contain arrays of clear, brief, actionable strings.
                """.formatted(
                activityDto.getActivity(),
                activityDto.getDurationInSeconds(),
                activityDto.getDurationInSeconds() / 60.0,
                activityDto.getCaloriesBurned(),
                activityDto.getStartTime(),
                metricsText
        );
    }

    // Inject your ObjectMapper bean

    public Recommendation parseAiOutput(String aiRawOutput, ActivityDto activityDto) {
        try {
            // 1. Clean potential markdown wrappers if the AI ignored instructions
            String cleanJson = aiRawOutput.trim();
            if (cleanJson.startsWith("```")) {
                cleanJson = cleanJson.replaceAll("^```json\\s*|\\s*```$", "");
            }

            // 2. Deserialize the JSON string into the temporary DTO
            AiResponseDto aiDto = objectMapper.readValue(cleanJson, AiResponseDto.class);

            // 3. Build the final entity, safely mapping the UUIDs from the source DTO
            return Recommendation.builder()
                    .activityId(activityDto.getId())
                    .userId(activityDto.getUserId())
                    .recommendation(aiDto.getRecommendation())
                    .safety(aiDto.getSafety())
                    .improvements(aiDto.getImprovements())
                    .suggestions(aiDto.getSuggestions())
                    .build();

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Failed to convert ActivityDto String IDs to valid UUIDs", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI JSON response into Recommendation entity", e);
        }
    }



    public String generateRecommendation(ActivityDto activityDto) {
        // 1. Build the payload
        OpenAIRequest requestBody = new OpenAIRequest(
                model,
                List.of(
                        new OpenAIMessage("system", getActivityPrompt()),
                        new OpenAIMessage("user", getUserPrompt(activityDto))
                )
        );

        // 2. Execute the synchronous POST request
        return webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .header("Content-Type","application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block(); // Blocks to return String synchronously
    }

    public String processAIResponse(String response){
//        use object mapper to get values from json returned by the ai
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            return root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asString();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }
}
