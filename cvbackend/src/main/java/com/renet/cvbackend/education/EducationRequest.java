package com.renet.cvbackend.education;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record EducationRequest(
		@NotBlank @Size(max = 255) String institution,
		@Size(max = 255) String degree,
		@Size(max = 255) String field,
		LocalDate startDate,
		LocalDate endDate,
		@Valid List<EducationBulletRequest> bullets
) {

	@AssertTrue(message = "endDate must be on or after startDate")
	public boolean isEndDateValid() {
		return endDate == null || startDate == null || !endDate.isBefore(startDate);
	}

}
