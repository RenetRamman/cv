package com.renet.cvbackend.cv;

import java.math.BigDecimal;

public record CvSkillResponse(
		Long id,
		String name,
		BigDecimal relevanceScore,
		boolean highlighted
) {
}
