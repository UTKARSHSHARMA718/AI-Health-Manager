package com.fitness.activity_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI().info(new Info().title("Fitness Microservice App | Activity Service")
                .version("1.0.0")
                .description("Application for practicing microservice in java")
                .contact(new Contact().name("Utkarsh Sharma")
                        .email("utkarsh79sharma@gmail.com"))
        );
    }
}
