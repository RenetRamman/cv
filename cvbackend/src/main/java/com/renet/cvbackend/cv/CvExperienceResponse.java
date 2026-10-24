package com.renet.cvbackend.cv;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CvExperienceResponse(
		Long id,
		String company,
		String title,
		String location,
		LocalDate startDate,
		LocalDate endDate,
		BigDecimal relevanceScore,
		List<CvBulletResponse> bullets
) {
}
