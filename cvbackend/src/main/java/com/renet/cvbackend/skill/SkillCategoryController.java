package com.renet.cvbackend.skill;

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
@RequestMapping("/api/skill-categories")
public class SkillCategoryController {

	private final SkillCategoryService skillCategoryService;

	public SkillCategoryController(SkillCategoryService skillCategoryService) {
		this.skillCategoryService = skillCategoryService;
	}

	@GetMapping
	public List<SkillCategoryResponse> listSkillCategories() {
		return skillCategoryService.listSkillCategories();
	}

	@GetMapping("/{id}")
	public SkillCategoryResponse getSkillCategory(@PathVariable Long id) {
		return skillCategoryService.getSkillCategory(id);
	}

	@PostMapping
	public ResponseEntity<SkillCategoryResponse> createSkillCategory(
			@Valid @RequestBody SkillCategoryRequest request
	) {
		var created = skillCategoryService.createSkillCategory(request);
		var location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(created.id())
				.toUri();

		return ResponseEntity.created(location).body(created);
	}

	@PutMapping("/{id}")
	public SkillCategoryResponse updateSkillCategory(
			@PathVariable Long id,
			@Valid @RequestBody SkillCategoryRequest request
	) {
		return skillCategoryService.updateSkillCategory(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSkillCategory(@PathVariable Long id) {
		skillCategoryService.deleteSkillCategory(id);
	}

}
