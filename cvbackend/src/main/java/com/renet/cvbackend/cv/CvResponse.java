package com.renet.cvbackend.cv;

import java.util.List;

public record CvResponse(
		String selectedRoleSlug,
		String selectedRoleName,
		String fullName,
		String headline,
		String email,
		String phone,
		String location,
		List<CvContactLinkResponse> contactLinks,
		List<CvRoleOptionResponse> roles,
		String introduction,
		List<CvExperienceResponse> experiences,
		List<CvEducationResponse> educations,
		List<CvSkillCategoryResponse> skillCategories,
		List<CvProjectResponse> projects
) {
}
