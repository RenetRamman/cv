package com.renet.cvbackend.roleprofile;

import java.util.List;

public record RoleProfileResponse(
		Long id,
		String slug,
		String name,
		RoleProfileType type,
		int sortOrder,
		List<RoleProfileTagResponse> tags
) {

	static RoleProfileResponse from(RoleProfile roleProfile) {
		var tags = roleProfile.getTags().stream()
				.map(RoleProfileTagResponse::from)
				.toList();

		return new RoleProfileResponse(
				roleProfile.getId(),
				roleProfile.getSlug(),
				roleProfile.getName(),
				roleProfile.getType(),
				roleProfile.getSortOrder(),
				tags
		);
	}

}
