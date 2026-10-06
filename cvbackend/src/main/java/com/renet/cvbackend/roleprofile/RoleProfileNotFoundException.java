package com.renet.cvbackend.roleprofile;

public class RoleProfileNotFoundException extends RuntimeException {

	public RoleProfileNotFoundException(Long id) {
		super("Role profile not found: " + id);
	}

}
