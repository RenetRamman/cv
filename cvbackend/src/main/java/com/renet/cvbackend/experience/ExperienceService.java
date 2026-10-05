package com.renet.cvbackend.experience;

import com.renet.cvbackend.profile.ProfileRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExperienceService {

	private final ExperienceRepository experienceRepository;
	private final ProfileRepository profileRepository;

	public ExperienceService(
			ExperienceRepository experienceRepository,
			ProfileRepository profileRepository
	) {
		this.experienceRepository = experienceRepository;
		this.profileRepository = profileRepository;
	}

	@Transactional(readOnly = true)
	public List<ExperienceResponse> listExperiences() {
		return experienceRepository.findAllByOrderByStartDateDescIdDesc().stream()
				.map(ExperienceResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public ExperienceResponse getExperience(Long id) {
		return ExperienceResponse.from(findExperience(id));
	}

	@Transactional
	public ExperienceResponse createExperience(ExperienceRequest request) {
		var profile = profileRepository.findFirstByOrderByIdAsc()
				.orElseThrow(ProfileRequiredException::new);

		var experience = new Experience();
		experience.setProfile(profile);
		applyRequest(experience, request);

		return ExperienceResponse.from(experienceRepository.save(experience));
	}

	@Transactional
	public ExperienceResponse updateExperience(Long id, ExperienceRequest request) {
		var experience = findExperience(id);
		applyRequest(experience, request);
		return ExperienceResponse.from(experienceRepository.save(experience));
	}

	@Transactional
	public void deleteExperience(Long id) {
		var experience = findExperience(id);
		experienceRepository.delete(experience);
	}

	private Experience findExperience(Long id) {
		return experienceRepository.findById(id)
				.orElseThrow(() -> new ExperienceNotFoundException(id));
	}

	private void applyRequest(Experience experience, ExperienceRequest request) {
		experience.setCompany(request.company());
		experience.setTitle(request.title());
		experience.setLocation(request.location());
		experience.setStartDate(request.startDate());
		experience.setEndDate(request.endDate());
		replaceBullets(experience, request.bullets());
	}

	private void replaceBullets(Experience experience, List<ExperienceBulletRequest> bullets) {
		experience.getBullets().clear();

		if (bullets == null) {
			return;
		}

		for (var bulletRequest : bullets) {
			var bullet = new ExperienceBullet();
			bullet.setExperience(experience);
			bullet.setContent(bulletRequest.content());
			experience.getBullets().add(bullet);
		}
	}

}
