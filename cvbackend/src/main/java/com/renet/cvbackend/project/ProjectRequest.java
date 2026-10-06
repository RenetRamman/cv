package com.renet.cvbackend.project;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ProjectRequest(
		@NotBlank @Size(max = 255) String title,
		String description,
		@Size(max = 2048) String githubUrl,
		@Size(max = 2048) String liveUrl,
		@Valid List<ProjectBulletRequest> bullets,
		@Valid List<ProjectTechnologyRequest> technologies
) {
}
