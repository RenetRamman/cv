package com.renet.cvbackend.cv;

import java.math.BigDecimal;
import java.util.List;

public record CvSkillCategoryResponse(
		Long id,
		String name,
		BigDecimal relevanceScore,
		List<CvSkillResponse> skills
) {
}
