package com.renet.cvbackend.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record SkillRequest(
		@NotBlank @Size(max = 255) String name,
		List<Long> tagIds
) {
}
