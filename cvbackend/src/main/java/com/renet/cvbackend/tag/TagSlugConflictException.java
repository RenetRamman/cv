package com.renet.cvbackend.tag;

public class TagSlugConflictException extends RuntimeException {

	public TagSlugConflictException(String slug) {
		super("Tag slug already exists: " + slug);
	}

}
