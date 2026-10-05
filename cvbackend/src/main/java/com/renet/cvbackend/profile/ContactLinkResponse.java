package com.renet.cvbackend.profile;

public record ContactLinkResponse(
		Long id,
		String label,
		String url
) {

	static ContactLinkResponse from(ContactLink contactLink) {
		return new ContactLinkResponse(
				contactLink.getId(),
				contactLink.getLabel(),
				contactLink.getUrl()
		);
	}

}
