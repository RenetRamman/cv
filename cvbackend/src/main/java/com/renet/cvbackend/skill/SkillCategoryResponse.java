package com.renet.cvbackend.skill;

import java.util.List;

public record SkillCategoryResponse(
		Long id,
		String name,
		List<SkillResponse> skills
) {

	static SkillCategoryResponse from(SkillCategory category) {
		var skills = category.getSkills().stream()
				.map(SkillResponse::from)
				.toList();

		return new SkillCategoryResponse(category.getId(), category.getName(), skills);
	}

}
