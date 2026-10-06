package com.renet.cvbackend.tag;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TagExceptionHandler {

	@ExceptionHandler(TagNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	Map<String, String> handleTagNotFound(TagNotFoundException exception) {
		return Map.of("message", exception.getMessage());
	}

	@ExceptionHandler(TagSlugConflictException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	Map<String, String> handleTagSlugConflict(TagSlugConflictException exception) {
		return Map.of("message", exception.getMessage());
	}

	@ExceptionHandler(TagInUseException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	Map<String, String> handleTagInUse(TagInUseException exception) {
		return Map.of("message", exception.getMessage());
	}

}
