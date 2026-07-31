package com.fitness.activity_service.Services;

import com.fitness.activity_service.dtos.ActivityDto;
import com.fitness.activity_service.dtos.CreateActivityRequest;
import com.fitness.activity_service.dtos.UpdateActivityRequest;
import com.fitness.activity_service.exceptions.custom.BadRequestException;
import com.fitness.activity_service.models.Activity;
import com.fitness.activity_service.repositories.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {
    private final KafkaTemplate<String, ActivityDto> kafkaTemplate;
    private final ActivityRepository activityRepository;
    private final ModelMapper modelMapper;

    @Value("${kafka.topic.name}")
    private String kafkaTopicName;

    public ActivityDto createActivity(CreateActivityRequest request){
        Activity newActivity = Activity.builder()
                .userId(request.getUserId()+"")
                .activity(request.getActivity()) // Replace with your enum value
                .durationInSeconds(request.getDurationInSeconds()) // 30 minutes
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();
        Activity savedActivity  = activityRepository.save(newActivity);

        ActivityDto activityDto = modelMapper.map(savedActivity, ActivityDto.class);

        try{
            log.info("Send the message to kafka");
            kafkaTemplate.send(kafkaTopicName, activityDto.getUserId() + "", activityDto);
        }catch (Exception e){
            e.printStackTrace();
        }

        return activityDto;
    }

    public List<ActivityDto> getActivitiesByUserId(String userId){
        List<Activity> activities =  activityRepository.findByUserId(userId);
        return activities.stream().map(act -> modelMapper.map(act, ActivityDto.class)).toList();
    }

    public ActivityDto getActivityById(String activityId){
        Optional<Activity> activity = activityRepository.findById(activityId);
        return modelMapper.map(activity, ActivityDto.class);
    }

    public ActivityDto deleteActivityById(String activityId){
        ActivityDto activity = getActivityById(activityId);
        if(activity==null){
            throw new BadRequestException("Invalid activity id:"+activityId);
        }
        activityRepository.deleteById(activityId);
        return modelMapper.map(activity, ActivityDto.class);
    }

    public ActivityDto updateActivity(String activityId, UpdateActivityRequest updatedActivityDto) {
        // 1. Fetch the actual database entity (or throw an exception if missing)
        Activity existingActivity = activityRepository.findById(activityId)
                .orElseThrow(() -> new BadRequestException("Activity not found with id: " + activityId));

        // 2. Map incoming DTO updates directly onto the existing entity
        existingActivity.setCaloriesBurned(updatedActivityDto.getCaloriesBurned());
        existingActivity.setAdditionalMetrics(updatedActivityDto.getAdditionalMetrics());
        existingActivity.setStartTime(updatedActivityDto.getStartTime());
        existingActivity.setUserId(updatedActivityDto.getUserId());
        existingActivity.setDurationInSeconds(updatedActivityDto.getDurationInSeconds());
        existingActivity.setActivity(updatedActivityDto.getActivity());

        // 3. Save the entity (Spring Auditing will automatically handle updatedAt)
        Activity savedActivity = activityRepository.save(existingActivity);

        // 4. Map the final saved entity back to a DTO for the return statement
        return modelMapper.map(savedActivity, ActivityDto.class);
    }


}
