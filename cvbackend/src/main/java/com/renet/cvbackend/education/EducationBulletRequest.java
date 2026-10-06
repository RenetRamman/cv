package com.renet.cvbackend.education;

import jakarta.validation.constraints.NotBlank;

public record EducationBulletRequest(
		@NotBlank String content
) {
}
