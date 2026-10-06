package com.renet.cvbackend.roleprofile;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RoleProfileExceptionHandler {

	@ExceptionHandler(RoleProfileNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	Map<String, String> handleRoleProfileNotFound(RoleProfileNotFoundException exception) {
		return Map.of("message", exception.getMessage());
	}

	@ExceptionHandler({
			RoleProfileSlugConflictException.class,
			RoleProfileGeneralConflictException.class,
			RoleProfileInUseException.class
	})
	@ResponseStatus(HttpStatus.CONFLICT)
	Map<String, String> handleConflict(RuntimeException exception) {
		return Map.of("message", exception.getMessage());
	}

	@ExceptionHandler(RoleProfileTagNotFoundException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	Map<String, String> handleTagNotFound(RoleProfileTagNotFoundException exception) {
		return Map.of("message", exception.getMessage());
	}

}
