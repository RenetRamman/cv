package com.renet.cvbackend.profile;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

	private final ProfileService profileService;

	public ProfileController(ProfileService profileService) {
		this.profileService = profileService;
	}

	@GetMapping
	public ProfileResponse getProfile() {
		return profileService.getProfile();
	}

	@PutMapping
	public ResponseEntity<ProfileResponse> upsertProfile(@Valid @RequestBody UpdateProfileRequest request) {
		ProfileUpsertResult result = profileService.upsertProfile(request);

		if (result.newlyCreated()) {
			URI location = URI.create("/api/profile");
			return ResponseEntity.created(location).body(result.profile());
		}

		return ResponseEntity.ok(result.profile());
	}

}
