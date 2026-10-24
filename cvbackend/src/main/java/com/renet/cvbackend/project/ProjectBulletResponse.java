package com.renet.cvbackend.project;

import com.renet.cvbackend.tag.ContentTagResponse;
import java.util.Comparator;
import java.util.List;

public record ProjectBulletResponse(
		Long id,
		String content,
		List<ContentTagResponse> tags
) {

	static ProjectBulletResponse from(ProjectBullet bullet) {
		var tags = bullet.getTags().stream()
				.map(ContentTagResponse::from)
				.sorted(Comparator.comparing(ContentTagResponse::slug))
				.toList();

		return new ProjectBulletResponse(bullet.getId(), bullet.getContent(), tags);
	}

}
