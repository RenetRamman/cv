package com.renet.cvbackend.cv;

public class CvRoleProfileNotFoundException extends RuntimeException {

	public CvRoleProfileNotFoundException(String slug) {
		super("Role profile not found: " + slug);
	}

}
