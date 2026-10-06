package com.renet.cvbackend.skill;

public record SkillResponse(
		Long id,
		String name
) {

	static SkillResponse from(Skill skill) {
		return new SkillResponse(skill.getId(), skill.getName());
	}

}
