package com.renet.cvbackend.experience;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExperienceExceptionHandler {

	@ExceptionHandler(ExperienceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	Map<String, String> handleExperienceNotFound(ExperienceNotFoundException exception) {
		return Map.of("message", exception.getMessage());
	}

	@ExceptionHandler(ProfileRequiredException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	Map<String, String> handleProfileRequired(ProfileRequiredException exception) {
		return Map.of("message", exception.getMessage());
	}

}
