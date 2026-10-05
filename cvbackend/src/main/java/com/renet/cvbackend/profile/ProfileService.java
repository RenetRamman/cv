package com.renet.cvbackend.profile;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

	private final ProfileRepository profileRepository;

	public ProfileService(ProfileRepository profileRepository) {
		this.profileRepository = profileRepository;
	}

	@Transactional(readOnly = true)
	public ProfileResponse getProfile() {
		return profileRepository.findFirstByOrderByIdAsc()
				.map(ProfileResponse::from)
				.orElseThrow(ProfileNotFoundException::new);
	}

	@Transactional
	public ProfileResponse upsertProfile(UpdateProfileRequest request) {
		var profile = profileRepository.findFirstByOrderByIdAsc()
				.orElseGet(Profile::new);

		profile.setFullName(request.fullName());
		profile.setHeadline(request.headline());
		profile.setEmail(request.email());
		profile.setPhone(request.phone());
		profile.setLocation(request.location());
		replaceContactLinks(profile, request.contactLinks());

		return ProfileResponse.from(profileRepository.save(profile));
	}

	private void replaceContactLinks(Profile profile, List<UpdateContactLinkRequest> contactLinks) {
		profile.getContactLinks().clear();

		if (contactLinks == null) {
			return;
		}

		for (var contactLinkRequest : contactLinks) {
			var contactLink = new ContactLink();
			contactLink.setProfile(profile);
			contactLink.setLabel(contactLinkRequest.label());
			contactLink.setUrl(contactLinkRequest.url());
			profile.getContactLinks().add(contactLink);
		}
	}

}
