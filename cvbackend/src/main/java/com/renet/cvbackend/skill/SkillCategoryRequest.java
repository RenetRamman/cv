package com.renet.cvbackend.skill;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record SkillCategoryRequest(
		@NotBlank @Size(max = 255) String name,
		@Valid List<SkillRequest> skills
) {
}
