package com.renet.cvbackend.skill;

import com.renet.cvbackend.tag.ContentTagResponse;
import java.util.Comparator;
import java.util.List;

public record SkillResponse(
		Long id,
		String name,
		List<ContentTagResponse> tags
) {

	static SkillResponse from(Skill skill) {
		var tags = skill.getTags().stream()
				.map(ContentTagResponse::from)
				.sorted(Comparator.comparing(ContentTagResponse::slug))
				.toList();

		return new SkillResponse(skill.getId(), skill.getName(), tags);
	}

}
