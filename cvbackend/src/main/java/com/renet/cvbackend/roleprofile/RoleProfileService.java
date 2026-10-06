package com.renet.cvbackend.roleprofile;

import com.renet.cvbackend.tag.TagRepository;
import java.util.HashSet;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleProfileService {

	private final RoleProfileRepository roleProfileRepository;
	private final TagRepository tagRepository;

	public RoleProfileService(
			RoleProfileRepository roleProfileRepository,
			TagRepository tagRepository
	) {
		this.roleProfileRepository = roleProfileRepository;
		this.tagRepository = tagRepository;
	}

	@Transactional(readOnly = true)
	public List<RoleProfileResponse> listRoleProfiles() {
		return roleProfileRepository.findAllByOrderBySortOrderAscIdAsc().stream()
				.map(RoleProfileResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public RoleProfileResponse getRoleProfile(Long id) {
		return RoleProfileResponse.from(findRoleProfile(id));
	}

	@Transactional
	public RoleProfileResponse createRoleProfile(RoleProfileRequest request) {
		if (roleProfileRepository.existsBySlug(request.slug())) {
			throw new RoleProfileSlugConflictException(request.slug());
		}
		ensureGeneralConstraint(request.type(), null);

		var roleProfile = new RoleProfile();
		applyRequest(roleProfile, request);
		return RoleProfileResponse.from(roleProfileRepository.save(roleProfile));
	}

	@Transactional
	public RoleProfileResponse updateRoleProfile(Long id, RoleProfileRequest request) {
		var roleProfile = findRoleProfile(id);

		if (roleProfileRepository.existsBySlugAndIdNot(request.slug(), id)) {
			throw new RoleProfileSlugConflictException(request.slug());
		}
		ensureGeneralConstraint(request.type(), id);

		applyRequest(roleProfile, request);
		return RoleProfileResponse.from(roleProfileRepository.save(roleProfile));
	}

	@Transactional
	public void deleteRoleProfile(Long id) {
		var roleProfile = findRoleProfile(id);
		try {
			roleProfileRepository.delete(roleProfile);
			roleProfileRepository.flush();
		}
		catch (DataIntegrityViolationException exception) {
			throw new RoleProfileInUseException(id);
		}
	}

	private RoleProfile findRoleProfile(Long id) {
		return roleProfileRepository.findById(id)
				.orElseThrow(() -> new RoleProfileNotFoundException(id));
	}

	private void ensureGeneralConstraint(RoleProfileType type, Long currentId) {
		if (type != RoleProfileType.GENERAL) {
			return;
		}

		boolean generalExists = currentId == null
				? roleProfileRepository.existsByType(RoleProfileType.GENERAL)
				: roleProfileRepository.existsByTypeAndIdNot(RoleProfileType.GENERAL, currentId);

		if (generalExists) {
			throw new RoleProfileGeneralConflictException();
		}
	}

	private void applyRequest(RoleProfile roleProfile, RoleProfileRequest request) {
		roleProfile.setSlug(request.slug());
		roleProfile.setName(request.name());
		roleProfile.setType(request.type());
		roleProfile.setSortOrder(request.sortOrder());
		replaceTags(roleProfile, request.tags());
	}

	private void replaceTags(RoleProfile roleProfile, List<RoleProfileTagRequest> tags) {
		roleProfile.getTags().clear();

		if (tags == null) {
			return;
		}

		var seenTagIds = new HashSet<Long>();
		for (var tagRequest : tags) {
			if (!seenTagIds.add(tagRequest.tagId())) {
				continue;
			}

			var tag = tagRepository.findById(tagRequest.tagId())
					.orElseThrow(() -> new RoleProfileTagNotFoundException(tagRequest.tagId()));

			var roleProfileTag = new RoleProfileTag();
			roleProfileTag.setRoleProfile(roleProfile);
			roleProfileTag.setTag(tag);
			roleProfileTag.setWeight(tagRequest.weight());
			roleProfile.getTags().add(roleProfileTag);
		}
	}

}
