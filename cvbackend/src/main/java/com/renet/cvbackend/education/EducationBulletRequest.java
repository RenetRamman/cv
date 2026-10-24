package com.renet.cvbackend.education;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record EducationBulletRequest(
		@NotBlank String content,
		List<Long> tagIds
) {
}
