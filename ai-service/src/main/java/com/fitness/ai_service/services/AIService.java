package com.fitness.ai_service.services;

import com.fitness.ai_service.dtos.RecommendationDto;
import com.fitness.ai_service.models.Recommendation;
import com.fitness.ai_service.repositories.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AIService {
    private final RecommendationRepository recommendationRepository;
    private final ModelMapper modelMapper;
    private final ActivityListener activityListener;

    public List<RecommendationDto> getRecommendationByUserId(String userId){
       List<Recommendation> list=  recommendationRepository.findByUserId(userId);
       return list.stream().map(r -> modelMapper.map(r, RecommendationDto.class)).toList();
    }

    public void regenerateRecommendation(String activityId){
//        activityListener.createRecommendation();
    }
}
