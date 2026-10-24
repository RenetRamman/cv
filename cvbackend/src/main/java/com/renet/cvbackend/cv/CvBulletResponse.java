package com.renet.cvbackend.cv;

import java.math.BigDecimal;

public record CvBulletResponse(
		Long id,
		String content,
		BigDecimal relevanceScore
) {
}
