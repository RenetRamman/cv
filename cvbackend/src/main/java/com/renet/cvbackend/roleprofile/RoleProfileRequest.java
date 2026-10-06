package com.renet.cvbackend.roleprofile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record RoleProfileRequest(
		@NotBlank
		@Size(max = 128)
		@Pattern(
				regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
				message = "slug must be lowercase letters, digits, and hyphens"
		)
		String slug,
		@NotBlank @Size(max = 255) String name,
		@NotNull RoleProfileType type,
		int sortOrder,
		@Valid List<RoleProfileTagRequest> tags
) {
}
