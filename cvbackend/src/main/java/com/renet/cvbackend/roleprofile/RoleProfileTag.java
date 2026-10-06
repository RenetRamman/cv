package com.renet.cvbackend.roleprofile;

import com.renet.cvbackend.tag.Tag;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "role_profile_tag")
public class RoleProfileTag {

	@EmbeddedId
	private RoleProfileTagId id = new RoleProfileTagId();

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@MapsId("roleProfileId")
	@JoinColumn(name = "role_profile_id", nullable = false)
	private RoleProfile roleProfile;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@MapsId("tagId")
	@JoinColumn(name = "tag_id", nullable = false)
	private Tag tag;

	@Column(nullable = false, precision = 4, scale = 3)
	private BigDecimal weight;

	public RoleProfileTagId getId() {
		return id;
	}

	public void setId(RoleProfileTagId id) {
		this.id = id;
	}

	public RoleProfile getRoleProfile() {
		return roleProfile;
	}

	public void setRoleProfile(RoleProfile roleProfile) {
		this.roleProfile = roleProfile;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

}
