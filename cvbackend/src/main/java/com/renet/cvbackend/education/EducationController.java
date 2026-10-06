package com.renet.cvbackend.education;

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
@RequestMapping("/api/educations")
public class EducationController {

	private final EducationService educationService;

	public EducationController(EducationService educationService) {
		this.educationService = educationService;
	}

	@GetMapping
	public List<EducationResponse> listEducations() {
		return educationService.listEducations();
	}

	@GetMapping("/{id}")
	public EducationResponse getEducation(@PathVariable Long id) {
		return educationService.getEducation(id);
	}

	@PostMapping
	public ResponseEntity<EducationResponse> createEducation(@Valid @RequestBody EducationRequest request) {
		var created = educationService.createEducation(request);
		var location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(created.id())
				.toUri();

		return ResponseEntity.created(location).body(created);
	}

	@PutMapping("/{id}")
	public EducationResponse updateEducation(
			@PathVariable Long id,
			@Valid @RequestBody EducationRequest request
	) {
		return educationService.updateEducation(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteEducation(@PathVariable Long id) {
		educationService.deleteEducation(id);
	}

}
