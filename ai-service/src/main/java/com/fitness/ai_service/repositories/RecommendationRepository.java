package com.fitness.ai_service.repositories;

import com.fitness.ai_service.models.Recommendation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecommendationRepository extends MongoRepository<Recommendation, UUID> {
    List<Recommendation> findByUserId(String userId);
}
