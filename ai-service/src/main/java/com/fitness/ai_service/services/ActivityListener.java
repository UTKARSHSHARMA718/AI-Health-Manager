package com.fitness.ai_service.services;

import com.fitness.ai_service.dtos.ActivityDto;
import com.fitness.ai_service.models.Recommendation;
import com.fitness.ai_service.repositories.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityListener {

    private final OpenAIService openAIService;
    private final RecommendationRepository recommendationRepository;

    @KafkaListener(topics = "${kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void ActivityProcessor(ActivityDto activityDto){
        log.info("kafka event: {}",  activityDto.toString());
        createRecommendation(activityDto);
    }

    public void createRecommendation(ActivityDto activityDto){
        String response =  openAIService.generateRecommendation(activityDto);
        String content = openAIService.processAIResponse(response);
        log.info("Content: {}", content);
        Recommendation recommendation = openAIService.parseAiOutput(content, activityDto);
        log.info("Recommendation ----------> : {}", recommendation);
        recommendationRepository.save(recommendation);
    }

}
