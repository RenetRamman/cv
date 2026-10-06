package com.renet.cvbackend.roleprofile;

public class RoleProfileGeneralConflictException extends RuntimeException {

	public RoleProfileGeneralConflictException() {
		super("Only one GENERAL role profile is allowed");
	}

}
