package com.renet.cvbackend.experience;

import java.time.LocalDate;
import java.util.List;

public record ExperienceResponse(
		Long id,
		String company,
		String title,
		String location,
		LocalDate startDate,
		LocalDate endDate,
		List<ExperienceBulletResponse> bullets
) {

	static ExperienceResponse from(Experience experience) {
		var bullets = experience.getBullets().stream()
				.map(ExperienceBulletResponse::from)
				.toList();

		return new ExperienceResponse(
				experience.getId(),
				experience.getCompany(),
				experience.getTitle(),
				experience.getLocation(),
				experience.getStartDate(),
				experience.getEndDate(),
				bullets
		);
	}

}
