package com.renet.cvbackend.tag;

public class TagInUseException extends RuntimeException {

	public TagInUseException(Long id) {
		super("Tag is still referenced by other records: " + id);
	}

}
