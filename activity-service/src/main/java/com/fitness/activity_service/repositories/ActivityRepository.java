package com.fitness.activity_service.repositories;

import com.fitness.activity_service.dtos.ActivityDto;
import com.fitness.activity_service.models.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityRepository extends MongoRepository<Activity, String> {
    List<Activity> findByUserId(String userId);
}
