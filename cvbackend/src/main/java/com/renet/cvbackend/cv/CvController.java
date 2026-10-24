package com.renet.cvbackend.cv;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cv")
public class CvController {

	private final CvService cvService;

	public CvController(CvService cvService) {
		this.cvService = cvService;
	}

	@GetMapping
	public CvResponse getCv(
			@RequestParam(name = "roleProfileSlug", required = false) String roleProfileSlug
	) {
		return cvService.getCv(roleProfileSlug);
	}

}
