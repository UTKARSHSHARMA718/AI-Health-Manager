package com.fitness.activity_service.controllers;

import com.fitness.activity_service.Services.ActivityService;
import com.fitness.activity_service.dtos.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
@Tag(
        name = "Activity Management",
        description = "APIs for creating and retrieving user fitness activities."
)
public class ActivityController {

    private final ActivityService activityService;

    @Operation(
            summary = "Create a new activity",
            description = "Creates a new activity for a user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Activity created successfully",
                    content = @Content(
                            schema = @Schema(implementation = ActivityResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<APIResponse<ActivityDto>> createActivity(
            @Valid @RequestBody CreateActivityRequest request
    ) {

        ActivityDto activity = activityService.createActivity(request);

        APIResponse<ActivityDto> response = APIResponse.<ActivityDto>builder()
                .success(true)
                .message("Activity has been created!")
                .body(activity)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get all activities of a user",
            description = "Returns all activities belonging to the specified user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Activities retrieved successfully",
                    content = @Content(
                            schema = @Schema(implementation = ActivityListResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User or activities not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    @GetMapping("/{userId}")
    public ResponseEntity<APIResponse<List<ActivityDto>>> getAllUserActivities(
            @Parameter(
                    description = "Unique identifier of the user",
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true
            )
            @PathVariable String userId
    ) {
        List<ActivityDto> activities = activityService.getActivitiesByUserId(userId);

        APIResponse<List<ActivityDto>> response = APIResponse.<List<ActivityDto>>builder()
                .success(true)
                .message("Activities retrieved successfully!")
                .body(activities)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{activityId}")
    public ResponseEntity<APIResponse<ActivityDto>> deleteActivityById(@PathVariable String activityId){
        ActivityDto activity = activityService.deleteActivityById(activityId);

        APIResponse<ActivityDto> response = APIResponse.<ActivityDto>builder()
                .success(true)
                .message("Activity has been deleted!")
                .body(activity)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{activityId}")
    public ResponseEntity<APIResponse<ActivityDto>> updateActivity(@PathVariable String activityId, @RequestBody UpdateActivityRequest request){
        ActivityDto activity = activityService.updateActivity(activityId, request);

        APIResponse<ActivityDto> response = APIResponse.<ActivityDto>builder()
                .success(true)
                .message("Activity has been updated!")
                .body(activity)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}