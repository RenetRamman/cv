package com.renet.cvbackend.project;

import jakarta.validation.constraints.NotBlank;

public record ProjectBulletRequest(
		@NotBlank String content
) {
}
