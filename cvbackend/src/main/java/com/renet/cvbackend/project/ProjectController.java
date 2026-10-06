package com.renet.cvbackend.project;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

	private final ProjectService projectService;

	public ProjectController(ProjectService projectService) {
		this.projectService = projectService;
	}

	@GetMapping
	public List<ProjectResponse> listProjects() {
		return projectService.listProjects();
	}

	@GetMapping("/{id}")
	public ProjectResponse getProject(@PathVariable Long id) {
		return projectService.getProject(id);
	}

	@PostMapping
	public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
		var created = projectService.createProject(request);
		var location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(created.id())
				.toUri();

		return ResponseEntity.created(location).body(created);
	}

	@PutMapping("/{id}")
	public ProjectResponse updateProject(
			@PathVariable Long id,
			@Valid @RequestBody ProjectRequest request
	) {
		return projectService.updateProject(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteProject(@PathVariable Long id) {
		projectService.deleteProject(id);
	}

}
