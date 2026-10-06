package com.renet.cvbackend.introduction;

public class IntroductionRoleProfileNotFoundException extends RuntimeException {

	public IntroductionRoleProfileNotFoundException(Long roleProfileId) {
		super("Role profile not found: " + roleProfileId);
	}

}
