package com.renet.cvbackend.skill;

import com.renet.cvbackend.profile.ProfileRepository;
import com.renet.cvbackend.profile.ProfileRequiredException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SkillCategoryService {

	private final SkillCategoryRepository skillCategoryRepository;
	private final ProfileRepository profileRepository;

	public SkillCategoryService(
			SkillCategoryRepository skillCategoryRepository,
			ProfileRepository profileRepository
	) {
		this.skillCategoryRepository = skillCategoryRepository;
		this.profileRepository = profileRepository;
	}

	@Transactional(readOnly = true)
	public List<SkillCategoryResponse> listSkillCategories() {
		return skillCategoryRepository.findAllByOrderByIdAsc().stream()
				.map(SkillCategoryResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public SkillCategoryResponse getSkillCategory(Long id) {
		return SkillCategoryResponse.from(findSkillCategory(id));
	}

	@Transactional
	public SkillCategoryResponse createSkillCategory(SkillCategoryRequest request) {
		var profile = profileRepository.findFirstByOrderByIdAsc()
				.orElseThrow(ProfileRequiredException::new);

		var category = new SkillCategory();
		category.setProfile(profile);
		applyRequest(category, request);

		return SkillCategoryResponse.from(skillCategoryRepository.save(category));
	}

	@Transactional
	public SkillCategoryResponse updateSkillCategory(Long id, SkillCategoryRequest request) {
		var category = findSkillCategory(id);
		applyRequest(category, request);
		return SkillCategoryResponse.from(skillCategoryRepository.save(category));
	}

	@Transactional
	public void deleteSkillCategory(Long id) {
		var category = findSkillCategory(id);
		skillCategoryRepository.delete(category);
	}

	private SkillCategory findSkillCategory(Long id) {
		return skillCategoryRepository.findById(id)
				.orElseThrow(() -> new SkillCategoryNotFoundException(id));
	}

	private void applyRequest(SkillCategory category, SkillCategoryRequest request) {
		category.setName(request.name());
		replaceSkills(category, request.skills());
	}

	private void replaceSkills(SkillCategory category, List<SkillRequest> skills) {
		category.getSkills().clear();

		if (skills == null) {
			return;
		}

		for (var skillRequest : skills) {
			var skill = new Skill();
			skill.setCategory(category);
			skill.setName(skillRequest.name());
			category.getSkills().add(skill);
		}
	}

}
