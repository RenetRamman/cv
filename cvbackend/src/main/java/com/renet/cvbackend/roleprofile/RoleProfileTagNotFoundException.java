package com.renet.cvbackend.roleprofile;

public class RoleProfileTagNotFoundException extends RuntimeException {

	public RoleProfileTagNotFoundException(Long tagId) {
		super("Tag not found: " + tagId);
	}

}
