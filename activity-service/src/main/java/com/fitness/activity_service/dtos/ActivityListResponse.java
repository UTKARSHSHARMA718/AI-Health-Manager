package com.fitness.activity_service.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "ActivityListResponse")
public class ActivityListResponse extends APIResponse<List<ActivityDto>>{
}
