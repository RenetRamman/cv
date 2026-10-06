package com.renet.cvbackend.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectTechnologyRequest(
		@NotBlank @Size(max = 255) String name
) {
}
