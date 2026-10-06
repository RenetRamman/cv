package com.renet.cvbackend.project;

public class ProjectNotFoundException extends RuntimeException {

	public ProjectNotFoundException(Long id) {
		super("Project not found: " + id);
	}

}
