package com.renet.cvbackend.profile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record UpdateProfileRequest(
		@NotBlank @Size(max = 255) String fullName,
		@Size(max = 255) String headline,
		@Size(max = 255) String email,
		@Size(max = 64) String phone,
		@Size(max = 255) String location,
		@Valid List<UpdateContactLinkRequest> contactLinks
) {
}
