package com.renet.cvbackend.introduction;

public class IntroductionRoleConflictException extends RuntimeException {

	public IntroductionRoleConflictException(Long roleProfileId) {
		super("Introduction already exists for role profile: " + roleProfileId);
	}

}
