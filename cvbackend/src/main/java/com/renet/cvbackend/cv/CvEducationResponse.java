package com.renet.cvbackend.cv;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CvEducationResponse(
		Long id,
		String institution,
		String degree,
		String field,
		LocalDate startDate,
		LocalDate endDate,
		BigDecimal relevanceScore,
		List<CvBulletResponse> bullets
) {
}
