package com.renet.cvbackend.cv;

import com.renet.cvbackend.education.Education;
import com.renet.cvbackend.education.EducationBullet;
import com.renet.cvbackend.education.EducationRepository;
import com.renet.cvbackend.experience.Experience;
import com.renet.cvbackend.experience.ExperienceBullet;
import com.renet.cvbackend.experience.ExperienceRepository;
import com.renet.cvbackend.introduction.IntroductionRepository;
import com.renet.cvbackend.profile.Profile;
import com.renet.cvbackend.profile.ProfileNotFoundException;
import com.renet.cvbackend.profile.ProfileRepository;
import com.renet.cvbackend.project.Project;
import com.renet.cvbackend.project.ProjectBullet;
import com.renet.cvbackend.project.ProjectRepository;
import com.renet.cvbackend.relevance.RelevanceService;
import com.renet.cvbackend.roleprofile.RoleProfile;
import com.renet.cvbackend.roleprofile.RoleProfileRepository;
import com.renet.cvbackend.roleprofile.RoleProfileType;
import com.renet.cvbackend.skill.Skill;
import com.renet.cvbackend.skill.SkillCategory;
import com.renet.cvbackend.skill.SkillCategoryRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CvService {

	private static final String DEFAULT_ROLE_SLUG = "general";

	private final ProfileRepository profileRepository;
	private final RoleProfileRepository roleProfileRepository;
	private final IntroductionRepository introductionRepository;
	private final ExperienceRepository experienceRepository;
	private final EducationRepository educationRepository;
	private final SkillCategoryRepository skillCategoryRepository;
	private final ProjectRepository projectRepository;
	private final RelevanceService relevanceService;

	public CvService(
			ProfileRepository profileRepository,
			RoleProfileRepository roleProfileRepository,
			IntroductionRepository introductionRepository,
			ExperienceRepository experienceRepository,
			EducationRepository educationRepository,
			SkillCategoryRepository skillCategoryRepository,
			ProjectRepository projectRepository,
			RelevanceService relevanceService
	) {
		this.profileRepository = profileRepository;
		this.roleProfileRepository = roleProfileRepository;
		this.introductionRepository = introductionRepository;
		this.experienceRepository = experienceRepository;
		this.educationRepository = educationRepository;
		this.skillCategoryRepository = skillCategoryRepository;
		this.projectRepository = projectRepository;
		this.relevanceService = relevanceService;
	}

	@Transactional(readOnly = true)
	public CvResponse getCv(String roleProfileSlug) {
		String slug = (roleProfileSlug == null || roleProfileSlug.isBlank())
				? DEFAULT_ROLE_SLUG
				: roleProfileSlug.trim();

		RoleProfile roleProfile = roleProfileRepository.findBySlug(slug)
				.orElseThrow(() -> new CvRoleProfileNotFoundException(slug));

		Profile profile = profileRepository.findFirstByOrderByIdAsc()
				.orElseThrow(ProfileNotFoundException::new);

		Map<Long, BigDecimal> weights = weightsFor(roleProfile);

		List<CvRoleOptionResponse> roles = roleProfileRepository.findAllByOrderBySortOrderAscIdAsc().stream()
				.filter(role -> role.getType() == RoleProfileType.PERMANENT)
				.map(role -> new CvRoleOptionResponse(role.getSlug(), role.getName(), role.getSortOrder()))
				.toList();

		String introduction = introductionRepository.findByRoleProfileId(roleProfile.getId())
				.map(intro -> intro.getContent())
				.orElseGet(() -> generalIntroduction());

		return new CvResponse(
				roleProfile.getSlug(),
				roleProfile.getName(),
				profile.getFullName(),
				profile.getHeadline(),
				profile.getEmail(),
				profile.getPhone(),
				profile.getLocation(),
				profile.getContactLinks().stream()
						.map(link -> new CvContactLinkResponse(link.getLabel(), link.getUrl()))
						.toList(),
				roles,
				introduction,
				mapExperiences(experienceRepository.findAllByOrderByStartDateDescIdDesc(), weights),
				mapEducations(educationRepository.findAllByOrderByStartDateDescIdDesc(), weights),
				mapSkillCategories(skillCategoryRepository.findAllByOrderByIdAsc(), weights),
				mapProjects(projectRepository.findAllByOrderByIdAsc(), weights)
		);
	}

	private String generalIntroduction() {
		return roleProfileRepository.findBySlug(DEFAULT_ROLE_SLUG)
				.flatMap(general -> introductionRepository.findByRoleProfileId(general.getId()))
				.map(intro -> intro.getContent())
				.orElse("");
	}

	private Map<Long, BigDecimal> weightsFor(RoleProfile roleProfile) {
		Map<Long, BigDecimal> weights = new HashMap<>();
		for (var roleTag : roleProfile.getTags()) {
			weights.put(roleTag.getTag().getId(), roleTag.getWeight());
		}
		return weights;
	}

	private List<CvExperienceResponse> mapExperiences(List<Experience> experiences, Map<Long, BigDecimal> weights) {
		record Scored(Experience experience, BigDecimal score, List<CvBulletResponse> bullets) {
		}

		return experiences.stream()
				.map(experience -> {
					List<CvBulletResponse> bullets = mapBullets(
							experience.getBullets(),
							ExperienceBullet::getId,
							ExperienceBullet::getContent,
							ExperienceBullet::getTags,
							weights
					);
					BigDecimal score = relevanceService.max(
							bullets.stream().map(CvBulletResponse::relevanceScore).toList()
					);
					return new Scored(experience, score, bullets);
				})
				.sorted(Comparator
						.comparing(Scored::score)
						.reversed()
						.thenComparing(
								scored -> scored.experience().getStartDate(),
								Comparator.nullsLast(Comparator.<LocalDate>reverseOrder())
						)
						.thenComparingLong(scored -> scored.experience().getId()))
				.map(scored -> new CvExperienceResponse(
						scored.experience().getId(),
						scored.experience().getCompany(),
						scored.experience().getTitle(),
						scored.experience().getLocation(),
						scored.experience().getStartDate(),
						scored.experience().getEndDate(),
						scored.score(),
						scored.bullets()
				))
				.toList();
	}

	private List<CvEducationResponse> mapEducations(List<Education> educations, Map<Long, BigDecimal> weights) {
		record Scored(Education education, BigDecimal score, List<CvBulletResponse> bullets) {
		}

		return educations.stream()
				.map(education -> {
					List<CvBulletResponse> bullets = mapBullets(
							education.getBullets(),
							EducationBullet::getId,
							EducationBullet::getContent,
							EducationBullet::getTags,
							weights
					);
					BigDecimal score = relevanceService.max(
							bullets.stream().map(CvBulletResponse::relevanceScore).toList()
					);
					return new Scored(education, score, bullets);
				})
				.sorted(Comparator
						.comparing(Scored::score)
						.reversed()
						.thenComparing(
								scored -> scored.education().getStartDate(),
								Comparator.nullsLast(Comparator.<LocalDate>reverseOrder())
						)
						.thenComparingLong(scored -> scored.education().getId()))
				.map(scored -> new CvEducationResponse(
						scored.education().getId(),
						scored.education().getInstitution(),
						scored.education().getDegree(),
						scored.education().getField(),
						scored.education().getStartDate(),
						scored.education().getEndDate(),
						scored.score(),
						scored.bullets()
				))
				.toList();
	}

	private List<CvSkillCategoryResponse> mapSkillCategories(
			List<SkillCategory> categories,
			Map<Long, BigDecimal> weights
	) {
		record ScoredCategory(SkillCategory category, BigDecimal score, List<CvSkillResponse> skills) {
		}

		return categories.stream()
				.map(category -> {
					List<CvSkillResponse> skills = relevanceService.sortByScoreDesc(
							category.getSkills(),
							skill -> relevanceService.score(skill.getTags(), weights),
							Skill::getId
					).stream()
							.map(skill -> {
								BigDecimal score = relevanceService.score(skill.getTags(), weights);
								return new CvSkillResponse(
										skill.getId(),
										skill.getName(),
										score,
										relevanceService.highlighted(score)
								);
							})
							.toList();
					BigDecimal score = relevanceService.max(
							skills.stream().map(CvSkillResponse::relevanceScore).toList()
					);
					return new ScoredCategory(category, score, skills);
				})
				.sorted(Comparator
						.comparing(ScoredCategory::score)
						.reversed()
						.thenComparingLong(scored -> scored.category().getId()))
				.map(scored -> new CvSkillCategoryResponse(
						scored.category().getId(),
						scored.category().getName(),
						scored.score(),
						scored.skills()
				))
				.toList();
	}

	private List<CvProjectResponse> mapProjects(List<Project> projects, Map<Long, BigDecimal> weights) {
		record Scored(Project project, BigDecimal score, List<CvBulletResponse> bullets) {
		}

		return projects.stream()
				.map(project -> {
					List<CvBulletResponse> bullets = mapBullets(
							project.getBullets(),
							ProjectBullet::getId,
							ProjectBullet::getContent,
							ProjectBullet::getTags,
							weights
					);
					BigDecimal score = relevanceService.max(
							bullets.stream().map(CvBulletResponse::relevanceScore).toList()
					);
					return new Scored(project, score, bullets);
				})
				.sorted(Comparator
						.comparing(Scored::score)
						.reversed()
						.thenComparingLong(scored -> scored.project().getId()))
				.map(scored -> new CvProjectResponse(
						scored.project().getId(),
						scored.project().getTitle(),
						scored.project().getDescription(),
						scored.project().getGithubUrl(),
						scored.project().getLiveUrl(),
						scored.score(),
						scored.bullets(),
						scored.project().getTechnologies().stream()
								.map(tech -> tech.getName())
								.toList()
				))
				.toList();
	}

	private <T> List<CvBulletResponse> mapBullets(
			List<T> bullets,
			java.util.function.ToLongFunction<T> idFn,
			java.util.function.Function<T, String> contentFn,
			java.util.function.Function<T, java.util.Set<com.renet.cvbackend.tag.Tag>> tagsFn,
			Map<Long, BigDecimal> weights
	) {
		return relevanceService.sortByScoreDesc(
				bullets,
				bullet -> relevanceService.score(tagsFn.apply(bullet), weights),
				idFn
		).stream()
				.map(bullet -> new CvBulletResponse(
						idFn.applyAsLong(bullet),
						contentFn.apply(bullet),
						relevanceService.score(tagsFn.apply(bullet), weights)
				))
				.toList();
	}

}
