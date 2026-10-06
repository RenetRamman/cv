package com.renet.cvbackend.education;

public record EducationBulletResponse(
		Long id,
		String content
) {

	static EducationBulletResponse from(EducationBullet bullet) {
		return new EducationBulletResponse(bullet.getId(), bullet.getContent());
	}

}
