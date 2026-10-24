package com.renet.cvbackend.tag;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ContentTagAssigner {

	private final TagRepository tagRepository;

	public ContentTagAssigner(TagRepository tagRepository) {
		this.tagRepository = tagRepository;
	}

	public void replaceTags(Collection<Tag> target, List<Long> tagIds) {
		target.clear();

		if (tagIds == null || tagIds.isEmpty()) {
			return;
		}

		Set<Long> seen = new LinkedHashSet<>();
		for (Long tagId : tagIds) {
			if (tagId == null || !seen.add(tagId)) {
				continue;
			}

			var tag = tagRepository.findById(tagId)
					.orElseThrow(() -> new ContentTagNotFoundException(tagId));
			target.add(tag);
		}
	}

}
