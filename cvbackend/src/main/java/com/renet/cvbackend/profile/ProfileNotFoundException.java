package com.renet.cvbackend.profile;

public class ProfileNotFoundException extends RuntimeException {

	public ProfileNotFoundException() {
		super("Profile not found");
	}

}
