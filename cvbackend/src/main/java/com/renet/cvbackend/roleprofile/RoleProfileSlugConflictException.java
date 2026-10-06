package com.renet.cvbackend.roleprofile;

public class RoleProfileSlugConflictException extends RuntimeException {

	public RoleProfileSlugConflictException(String slug) {
		super("Role profile slug already exists: " + slug);
	}

}
