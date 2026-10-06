package com.renet.cvbackend.roleprofile;

public class RoleProfileInUseException extends RuntimeException {

	public RoleProfileInUseException(Long id) {
		super("Role profile is still referenced by other records: " + id);
	}

}
