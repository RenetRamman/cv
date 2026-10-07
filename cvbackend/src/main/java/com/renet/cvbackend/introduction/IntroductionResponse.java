package com.renet.cvbackend.introduction;

import com.renet.cvbackend.roleprofile.RoleProfileType;

public record IntroductionResponse(
		Long id,
		Long roleProfileId,
		String roleProfileSlug,
		String roleProfileName,
		RoleProfileType roleProfileType,
		String content
) {

	static IntroductionResponse from(Introduction introduction) {
		var roleProfile = introduction.getRoleProfile();
		return new IntroductionResponse(
				introduction.getId(),
				roleProfile.getId(),
				roleProfile.getSlug(),
				roleProfile.getName(),
				roleProfile.getType(),
				introduction.getContent()
		);
	}

}
