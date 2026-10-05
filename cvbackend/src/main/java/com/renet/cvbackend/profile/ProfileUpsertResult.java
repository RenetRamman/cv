package com.renet.cvbackend.profile;

public record ProfileUpsertResult(
		ProfileResponse profile,
		boolean newlyCreated
) {
}
