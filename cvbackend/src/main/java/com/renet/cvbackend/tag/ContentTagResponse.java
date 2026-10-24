package com.renet.cvbackend.tag;

public record ContentTagResponse(
		Long tagId,
		String slug,
		String name
) {

	public static ContentTagResponse from(Tag tag) {
		return new ContentTagResponse(tag.getId(), tag.getSlug(), tag.getName());
	}

}
