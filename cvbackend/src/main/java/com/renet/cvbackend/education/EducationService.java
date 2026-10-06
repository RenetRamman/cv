package com.renet.cvbackend.education;

import com.renet.cvbackend.profile.ProfileRepository;
import com.renet.cvbackend.profile.ProfileRequiredException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EducationService {

	private final EducationRepository educationRepository;
	private final ProfileRepository profileRepository;

	public EducationService(
			EducationRepository educationRepository,
			ProfileRepository profileRepository
	) {
		this.educationRepository = educationRepository;
		this.profileRepository = profileRepository;
	}

	@Transactional(readOnly = true)
	public List<EducationResponse> listEducations() {
		return educationRepository.findAllByOrderByStartDateDescIdDesc().stream()
				.map(EducationResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public EducationResponse getEducation(Long id) {
		return EducationResponse.from(findEducation(id));
	}

	@Transactional
	public EducationResponse createEducation(EducationRequest request) {
		var profile = profileRepository.findFirstByOrderByIdAsc()
				.orElseThrow(ProfileRequiredException::new);

		var education = new Education();
		education.setProfile(profile);
		applyRequest(education, request);

		return EducationResponse.from(educationRepository.save(education));
	}

	@Transactional
	public EducationResponse updateEducation(Long id, EducationRequest request) {
		var education = findEducation(id);
		applyRequest(education, request);
		return EducationResponse.from(educationRepository.save(education));
	}

	@Transactional
	public void deleteEducation(Long id) {
		var education = findEducation(id);
		educationRepository.delete(education);
	}

	private Education findEducation(Long id) {
		return educationRepository.findById(id)
				.orElseThrow(() -> new EducationNotFoundException(id));
	}

	private void applyRequest(Education education, EducationRequest request) {
		education.setInstitution(request.institution());
		education.setDegree(request.degree());
		education.setField(request.field());
		education.setStartDate(request.startDate());
		education.setEndDate(request.endDate());
		replaceBullets(education, request.bullets());
	}

	private void replaceBullets(Education education, List<EducationBulletRequest> bullets) {
		education.getBullets().clear();

		if (bullets == null) {
			return;
		}

		for (var bulletRequest : bullets) {
			var bullet = new EducationBullet();
			bullet.setEducation(education);
			bullet.setContent(bulletRequest.content());
			education.getBullets().add(bullet);
		}
	}

}
