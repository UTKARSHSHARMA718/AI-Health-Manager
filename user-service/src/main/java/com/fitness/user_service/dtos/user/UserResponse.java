package com.fitness.user_service.dtos.user;

import com.fitness.user_service.dtos.APIResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserResponse")
public class UserResponse extends APIResponse<UserDto> {
}
