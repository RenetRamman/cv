package com.renet.cvbackend.introduction;

import com.renet.cvbackend.profile.ProfileRepository;
import com.renet.cvbackend.profile.ProfileRequiredException;
import com.renet.cvbackend.roleprofile.RoleProfileRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IntroductionService {

	private final IntroductionRepository introductionRepository;
	private final ProfileRepository profileRepository;
	private final RoleProfileRepository roleProfileRepository;

	public IntroductionService(
			IntroductionRepository introductionRepository,
			ProfileRepository profileRepository,
			RoleProfileRepository roleProfileRepository
	) {
		this.introductionRepository = introductionRepository;
		this.profileRepository = profileRepository;
		this.roleProfileRepository = roleProfileRepository;
	}

	@Transactional(readOnly = true)
	public List<IntroductionResponse> listIntroductions() {
		return introductionRepository.findAllByOrderByIdAsc().stream()
				.map(IntroductionResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public IntroductionResponse getIntroduction(Long id) {
		return IntroductionResponse.from(findIntroduction(id));
	}

	@Transactional
	public IntroductionResponse createIntroduction(IntroductionRequest request) {
		var profile = profileRepository.findFirstByOrderByIdAsc()
				.orElseThrow(ProfileRequiredException::new);
		var roleProfile = roleProfileRepository.findById(request.roleProfileId())
				.orElseThrow(() -> new IntroductionRoleProfileNotFoundException(request.roleProfileId()));

		if (introductionRepository.existsByRoleProfileId(roleProfile.getId())) {
			throw new IntroductionRoleConflictException(roleProfile.getId());
		}

		var introduction = new Introduction();
		introduction.setProfile(profile);
		introduction.setRoleProfile(roleProfile);
		introduction.setContent(request.content());

		return IntroductionResponse.from(introductionRepository.save(introduction));
	}

	@Transactional
	public IntroductionResponse updateIntroduction(Long id, IntroductionRequest request) {
		var introduction = findIntroduction(id);
		var roleProfile = roleProfileRepository.findById(request.roleProfileId())
				.orElseThrow(() -> new IntroductionRoleProfileNotFoundException(request.roleProfileId()));

		if (introductionRepository.existsByRoleProfileIdAndIdNot(roleProfile.getId(), id)) {
			throw new IntroductionRoleConflictException(roleProfile.getId());
		}

		introduction.setRoleProfile(roleProfile);
		introduction.setContent(request.content());

		return IntroductionResponse.from(introductionRepository.save(introduction));
	}

	@Transactional
	public void deleteIntroduction(Long id) {
		var introduction = findIntroduction(id);
		introductionRepository.delete(introduction);
	}

	private Introduction findIntroduction(Long id) {
		return introductionRepository.findById(id)
				.orElseThrow(() -> new IntroductionNotFoundException(id));
	}

}
