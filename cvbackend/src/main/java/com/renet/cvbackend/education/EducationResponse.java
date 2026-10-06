package com.renet.cvbackend.education;

import java.time.LocalDate;
import java.util.List;

public record EducationResponse(
		Long id,
		String institution,
		String degree,
		String field,
		LocalDate startDate,
		LocalDate endDate,
		List<EducationBulletResponse> bullets
) {

	static EducationResponse from(Education education) {
		var bullets = education.getBullets().stream()
				.map(EducationBulletResponse::from)
				.toList();

		return new EducationResponse(
				education.getId(),
				education.getInstitution(),
				education.getDegree(),
				education.getField(),
				education.getStartDate(),
				education.getEndDate(),
				bullets
		);
	}

}
