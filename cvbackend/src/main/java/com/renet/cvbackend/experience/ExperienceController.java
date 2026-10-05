package com.renet.cvbackend.experience;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/experiences")
public class ExperienceController {

	private final ExperienceService experienceService;

	public ExperienceController(ExperienceService experienceService) {
		this.experienceService = experienceService;
	}

	@GetMapping
	public List<ExperienceResponse> listExperiences() {
		return experienceService.listExperiences();
	}

	@GetMapping("/{id}")
	public ExperienceResponse getExperience(@PathVariable Long id) {
		return experienceService.getExperience(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ExperienceResponse createExperience(@Valid @RequestBody ExperienceRequest request) {
		return experienceService.createExperience(request);
	}

	@PutMapping("/{id}")
	public ExperienceResponse updateExperience(
			@PathVariable Long id,
			@Valid @RequestBody ExperienceRequest request
	) {
		return experienceService.updateExperience(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteExperience(@PathVariable Long id) {
		experienceService.deleteExperience(id);
	}

}
