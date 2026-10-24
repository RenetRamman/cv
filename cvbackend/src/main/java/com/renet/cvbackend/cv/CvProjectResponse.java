package com.renet.cvbackend.cv;

import java.math.BigDecimal;
import java.util.List;

public record CvProjectResponse(
		Long id,
		String title,
		String description,
		String githubUrl,
		String liveUrl,
		BigDecimal relevanceScore,
		List<CvBulletResponse> bullets,
		List<String> technologies
) {
}
