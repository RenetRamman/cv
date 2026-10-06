package com.renet.cvbackend.roleprofile;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RoleProfileTagRequest(
		@NotNull Long tagId,
		@NotNull
		@DecimalMin("0.000")
		@DecimalMax("1.000")
		BigDecimal weight
) {
}
