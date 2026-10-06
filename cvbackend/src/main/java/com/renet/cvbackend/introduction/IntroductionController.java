package com.renet.cvbackend.introduction;

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
@RequestMapping("/api/introductions")
public class IntroductionController {

	private final IntroductionService introductionService;

	public IntroductionController(IntroductionService introductionService) {
		this.introductionService = introductionService;
	}

	@GetMapping
	public List<IntroductionResponse> listIntroductions() {
		return introductionService.listIntroductions();
	}

	@GetMapping("/{id}")
	public IntroductionResponse getIntroduction(@PathVariable Long id) {
		return introductionService.getIntroduction(id);
	}

	@PostMapping
	public ResponseEntity<IntroductionResponse> createIntroduction(
			@Valid @RequestBody IntroductionRequest request
	) {
		var created = introductionService.createIntroduction(request);
		var location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(created.id())
				.toUri();

		return ResponseEntity.created(location).body(created);
	}

	@PutMapping("/{id}")
	public IntroductionResponse updateIntroduction(
			@PathVariable Long id,
			@Valid @RequestBody IntroductionRequest request
	) {
		return introductionService.updateIntroduction(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteIntroduction(@PathVariable Long id) {
		introductionService.deleteIntroduction(id);
	}

}
