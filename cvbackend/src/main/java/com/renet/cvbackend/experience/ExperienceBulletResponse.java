package com.renet.cvbackend.experience;

import com.renet.cvbackend.tag.ContentTagResponse;
import java.util.Comparator;
import java.util.List;

public record ExperienceBulletResponse(
		Long id,
		String content,
		List<ContentTagResponse> tags
) {

	static ExperienceBulletResponse from(ExperienceBullet bullet) {
		var tags = bullet.getTags().stream()
				.map(ContentTagResponse::from)
				.sorted(Comparator.comparing(ContentTagResponse::slug))
				.toList();

		return new ExperienceBulletResponse(bullet.getId(), bullet.getContent(), tags);
	}

}
