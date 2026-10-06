package com.renet.cvbackend.skill;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SkillCategoryExceptionHandler {

	@ExceptionHandler(SkillCategoryNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	Map<String, String> handleSkillCategoryNotFound(SkillCategoryNotFoundException exception) {
		return Map.of("message", exception.getMessage());
	}

}
