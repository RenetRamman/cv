package com.renet.cvbackend.roleprofile;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RoleProfileTagId implements Serializable {

	@Column(name = "role_profile_id")
	private Long roleProfileId;

	@Column(name = "tag_id")
	private Long tagId;

	public RoleProfileTagId() {
	}

	public RoleProfileTagId(Long roleProfileId, Long tagId) {
		this.roleProfileId = roleProfileId;
		this.tagId = tagId;
	}

	public Long getRoleProfileId() {
		return roleProfileId;
	}

	public void setRoleProfileId(Long roleProfileId) {
		this.roleProfileId = roleProfileId;
	}

	public Long getTagId() {
		return tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RoleProfileTagId that)) {
			return false;
		}
		return Objects.equals(roleProfileId, that.roleProfileId)
				&& Objects.equals(tagId, that.tagId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(roleProfileId, tagId);
	}

}
