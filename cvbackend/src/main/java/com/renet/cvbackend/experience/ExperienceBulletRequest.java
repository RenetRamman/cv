package com.renet.cvbackend.experience;

import jakarta.validation.constraints.NotBlank;

public record ExperienceBulletRequest(
		@NotBlank String content
) {
}
