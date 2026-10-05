package com.renet.cvbackend.experience;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record ExperienceRequest(
		@NotBlank @Size(max = 255) String company,
		@NotBlank @Size(max = 255) String title,
		@Size(max = 255) String location,
		@NotNull LocalDate startDate,
		LocalDate endDate,
		@Valid List<ExperienceBulletRequest> bullets
) {

	@AssertTrue(message = "endDate must be on or after startDate")
	public boolean isEndDateValid() {
		return endDate == null || startDate == null || !endDate.isBefore(startDate);
	}

}
