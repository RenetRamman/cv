package com.renet.cvbackend.tag;

public class ContentTagNotFoundException extends RuntimeException {

	public ContentTagNotFoundException(Long tagId) {
		super("Tag not found: " + tagId);
	}

}
