package com.renet.cvbackend.roleprofile;

import java.math.BigDecimal;

public record RoleProfileTagResponse(
		Long tagId,
		String slug,
		String name,
		BigDecimal weight
) {

	static RoleProfileTagResponse from(RoleProfileTag roleProfileTag) {
		var tag = roleProfileTag.getTag();
		return new RoleProfileTagResponse(
				tag.getId(),
				tag.getSlug(),
				tag.getName(),
				roleProfileTag.getWeight()
		);
	}

}
