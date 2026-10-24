package com.renet.cvbackend.education;

import com.renet.cvbackend.tag.ContentTagResponse;
import java.util.Comparator;
import java.util.List;

public record EducationBulletResponse(
		Long id,
		String content,
		List<ContentTagResponse> tags
) {

	static EducationBulletResponse from(EducationBullet bullet) {
		var tags = bullet.getTags().stream()
				.map(ContentTagResponse::from)
				.sorted(Comparator.comparing(ContentTagResponse::slug))
				.toList();

		return new EducationBulletResponse(bullet.getId(), bullet.getContent(), tags);
	}

}
