package com.renet.cvbackend.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateContactLinkRequest(
		@NotBlank @Size(max = 255) String label,
		@NotBlank @Size(max = 2048) String url
) {
}
