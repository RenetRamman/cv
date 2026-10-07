package com.renet.cvbackend.introduction;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class IntroductionExceptionHandler {

	@ExceptionHandler(IntroductionNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	Map<String, String> handleIntroductionNotFound(IntroductionNotFoundException exception) {
		return Map.of("message", exception.getMessage());
	}

	@ExceptionHandler(IntroductionRoleConflictException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	Map<String, String> handleRoleConflict(IntroductionRoleConflictException exception) {
		return Map.of("message", exception.getMessage());
	}

	@ExceptionHandler(IntroductionRoleProfileNotFoundException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	Map<String, String> handleRoleProfileNotFound(IntroductionRoleProfileNotFoundException exception) {
		return Map.of("message", exception.getMessage());
	}

}
