package com.renet.cvbackend.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TagRequest(
		@NotBlank
		@Size(max = 128)
		@Pattern(
				regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
				message = "slug must be lowercase letters, digits, and hyphens"
		)
		String slug,
		@NotBlank @Size(max = 255) String name
) {
}
