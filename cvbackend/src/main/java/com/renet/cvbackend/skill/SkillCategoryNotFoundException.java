package com.renet.cvbackend.skill;

public class SkillCategoryNotFoundException extends RuntimeException {

	public SkillCategoryNotFoundException(Long id) {
		super("Skill category not found: " + id);
	}

}
