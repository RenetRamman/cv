package com.renet.cvbackend.profile;

import java.util.List;

public record ProfileResponse(
		Long id,
		String fullName,
		String headline,
		String email,
		String phone,
		String location,
		List<ContactLinkResponse> contactLinks
) {

	static ProfileResponse from(Profile profile) {
		var contactLinks = profile.getContactLinks().stream()
				.map(ContactLinkResponse::from)
				.toList();

		return new ProfileResponse(
				profile.getId(),
				profile.getFullName(),
				profile.getHeadline(),
				profile.getEmail(),
				profile.getPhone(),
				profile.getLocation(),
				contactLinks
		);
	}

}
