package com.renet.cvbackend.introduction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IntroductionRequest(
		@NotNull Long roleProfileId,
		@NotBlank String content
) {
}
