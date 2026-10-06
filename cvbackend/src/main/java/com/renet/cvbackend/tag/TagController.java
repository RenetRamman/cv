package com.renet.cvbackend.tag;

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
@RequestMapping("/api/tags")
public class TagController {

	private final TagService tagService;

	public TagController(TagService tagService) {
		this.tagService = tagService;
	}

	@GetMapping
	public List<TagResponse> listTags() {
		return tagService.listTags();
	}

	@GetMapping("/{id}")
	public TagResponse getTag(@PathVariable Long id) {
		return tagService.getTag(id);
	}

	@PostMapping
	public ResponseEntity<TagResponse> createTag(@Valid @RequestBody TagRequest request) {
		var created = tagService.createTag(request);
		var location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(created.id())
				.toUri();

		return ResponseEntity.created(location).body(created);
	}

	@PutMapping("/{id}")
	public TagResponse updateTag(@PathVariable Long id, @Valid @RequestBody TagRequest request) {
		return tagService.updateTag(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteTag(@PathVariable Long id) {
		tagService.deleteTag(id);
	}

}
