package com.renet.cvbackend.profile;

public class ProfileRequiredException extends RuntimeException {

	public ProfileRequiredException() {
		super("Profile not found");
	}

}
