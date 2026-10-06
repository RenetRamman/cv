package com.renet.cvbackend.roleprofile;

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
@RequestMapping("/api/role-profiles")
public class RoleProfileController {

	private final RoleProfileService roleProfileService;

	public RoleProfileController(RoleProfileService roleProfileService) {
		this.roleProfileService = roleProfileService;
	}

	@GetMapping
	public List<RoleProfileResponse> listRoleProfiles() {
		return roleProfileService.listRoleProfiles();
	}

	@GetMapping("/{id}")
	public RoleProfileResponse getRoleProfile(@PathVariable Long id) {
		return roleProfileService.getRoleProfile(id);
	}

	@PostMapping
	public ResponseEntity<RoleProfileResponse> createRoleProfile(
			@Valid @RequestBody RoleProfileRequest request
	) {
		var created = roleProfileService.createRoleProfile(request);
		var location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(created.id())
				.toUri();

		return ResponseEntity.created(location).body(created);
	}

	@PutMapping("/{id}")
	public RoleProfileResponse updateRoleProfile(
			@PathVariable Long id,
			@Valid @RequestBody RoleProfileRequest request
	) {
		return roleProfileService.updateRoleProfile(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteRoleProfile(@PathVariable Long id) {
		roleProfileService.deleteRoleProfile(id);
	}

}
