package com.renet.cvbackend.education;

public class EducationNotFoundException extends RuntimeException {

	public EducationNotFoundException(Long id) {
		super("Education not found: " + id);
	}

}
