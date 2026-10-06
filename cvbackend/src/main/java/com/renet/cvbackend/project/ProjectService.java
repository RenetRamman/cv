package com.renet.cvbackend.project;

import com.renet.cvbackend.profile.ProfileRepository;
import com.renet.cvbackend.profile.ProfileRequiredException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

	private final ProjectRepository projectRepository;
	private final ProfileRepository profileRepository;

	public ProjectService(ProjectRepository projectRepository, ProfileRepository profileRepository) {
		this.projectRepository = projectRepository;
		this.profileRepository = profileRepository;
	}

	@Transactional(readOnly = true)
	public List<ProjectResponse> listProjects() {
		return projectRepository.findAllByOrderByIdAsc().stream()
				.map(ProjectResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public ProjectResponse getProject(Long id) {
		return ProjectResponse.from(findProject(id));
	}

	@Transactional
	public ProjectResponse createProject(ProjectRequest request) {
		var profile = profileRepository.findFirstByOrderByIdAsc()
				.orElseThrow(ProfileRequiredException::new);

		var project = new Project();
		project.setProfile(profile);
		applyRequest(project, request);

		return ProjectResponse.from(projectRepository.save(project));
	}

	@Transactional
	public ProjectResponse updateProject(Long id, ProjectRequest request) {
		var project = findProject(id);
		applyRequest(project, request);
		return ProjectResponse.from(projectRepository.save(project));
	}

	@Transactional
	public void deleteProject(Long id) {
		var project = findProject(id);
		projectRepository.delete(project);
	}

	private Project findProject(Long id) {
		return projectRepository.findById(id)
				.orElseThrow(() -> new ProjectNotFoundException(id));
	}

	private void applyRequest(Project project, ProjectRequest request) {
		project.setTitle(request.title());
		project.setDescription(request.description());
		project.setGithubUrl(request.githubUrl());
		project.setLiveUrl(request.liveUrl());
		replaceBullets(project, request.bullets());
		replaceTechnologies(project, request.technologies());
	}

	private void replaceBullets(Project project, List<ProjectBulletRequest> bullets) {
		project.getBullets().clear();

		if (bullets == null) {
			return;
		}

		for (var bulletRequest : bullets) {
			var bullet = new ProjectBullet();
			bullet.setProject(project);
			bullet.setContent(bulletRequest.content());
			project.getBullets().add(bullet);
		}
	}

	private void replaceTechnologies(Project project, List<ProjectTechnologyRequest> technologies) {
		project.getTechnologies().clear();

		if (technologies == null) {
			return;
		}

		for (var technologyRequest : technologies) {
			var technology = new ProjectTechnology();
			technology.setProject(project);
			technology.setName(technologyRequest.name());
			project.getTechnologies().add(technology);
		}
	}

}
