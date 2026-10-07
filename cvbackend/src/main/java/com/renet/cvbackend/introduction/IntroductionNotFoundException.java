package com.renet.cvbackend.introduction;

public class IntroductionNotFoundException extends RuntimeException {

	public IntroductionNotFoundException(Long id) {
		super("Introduction not found: " + id);
	}

}
