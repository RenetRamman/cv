package com.renet.cvbackend.experience;

public class ProfileRequiredException extends RuntimeException {

	public ProfileRequiredException() {
		super("Profile not found");
	}

}
