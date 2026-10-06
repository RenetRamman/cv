package com.renet.cvbackend.tag;

public record TagResponse(
		Long id,
		String slug,
		String name
) {

	static TagResponse from(Tag tag) {
		return new TagResponse(tag.getId(), tag.getSlug(), tag.getName());
	}

}
