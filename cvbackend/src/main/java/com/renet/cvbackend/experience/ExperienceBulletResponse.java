package com.renet.cvbackend.experience;

public record ExperienceBulletResponse(
		Long id,
		String content
) {

	static ExperienceBulletResponse from(ExperienceBullet bullet) {
		return new ExperienceBulletResponse(bullet.getId(), bullet.getContent());
	}

}
