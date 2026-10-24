package com.renet.cvbackend.experience;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record ExperienceBulletRequest(
		@NotBlank String content,
		List<Long> tagIds
) {
}
