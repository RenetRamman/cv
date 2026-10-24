package com.renet.cvbackend.project;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record ProjectBulletRequest(
		@NotBlank String content,
		List<Long> tagIds
) {
}
