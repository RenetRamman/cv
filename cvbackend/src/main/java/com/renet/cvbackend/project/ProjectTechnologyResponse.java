package com.renet.cvbackend.project;

public record ProjectTechnologyResponse(
		Long id,
		String name
) {

	static ProjectTechnologyResponse from(ProjectTechnology technology) {
		return new ProjectTechnologyResponse(technology.getId(), technology.getName());
	}

}
