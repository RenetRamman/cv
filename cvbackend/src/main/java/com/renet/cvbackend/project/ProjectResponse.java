package com.renet.cvbackend.project;

import java.util.List;

public record ProjectResponse(
		Long id,
		String title,
		String description,
		String githubUrl,
		String liveUrl,
		List<ProjectBulletResponse> bullets,
		List<ProjectTechnologyResponse> technologies
) {

	static ProjectResponse from(Project project) {
		var bullets = project.getBullets().stream()
				.map(ProjectBulletResponse::from)
				.toList();
		var technologies = project.getTechnologies().stream()
				.map(ProjectTechnologyResponse::from)
				.toList();

		return new ProjectResponse(
				project.getId(),
				project.getTitle(),
				project.getDescription(),
				project.getGithubUrl(),
				project.getLiveUrl(),
				bullets,
				technologies
		);
	}

}
