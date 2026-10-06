package com.renet.cvbackend.project;

public record ProjectBulletResponse(
		Long id,
		String content
) {

	static ProjectBulletResponse from(ProjectBullet bullet) {
		return new ProjectBulletResponse(bullet.getId(), bullet.getContent());
	}

}
