package com.fitness.activity_service.exceptions.custom;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message){
        super(message);
    }
}
