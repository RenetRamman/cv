package com.renet.cvbackend.tag;

import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService {

	private final TagRepository tagRepository;

	public TagService(TagRepository tagRepository) {
		this.tagRepository = tagRepository;
	}

	@Transactional(readOnly = true)
	public List<TagResponse> listTags() {
		return tagRepository.findAllByOrderBySlugAsc().stream()
				.map(TagResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public TagResponse getTag(Long id) {
		return TagResponse.from(findTag(id));
	}

	@Transactional
	public TagResponse createTag(TagRequest request) {
		if (tagRepository.existsBySlug(request.slug())) {
			throw new TagSlugConflictException(request.slug());
		}

		var tag = new Tag();
		applyRequest(tag, request);
		return TagResponse.from(tagRepository.save(tag));
	}

	@Transactional
	public TagResponse updateTag(Long id, TagRequest request) {
		var tag = findTag(id);

		if (tagRepository.existsBySlugAndIdNot(request.slug(), id)) {
			throw new TagSlugConflictException(request.slug());
		}

		applyRequest(tag, request);
		return TagResponse.from(tagRepository.save(tag));
	}

	@Transactional
	public void deleteTag(Long id) {
		var tag = findTag(id);
		try {
			tagRepository.delete(tag);
			tagRepository.flush();
		}
		catch (DataIntegrityViolationException exception) {
			throw new TagInUseException(id);
		}
	}

	private Tag findTag(Long id) {
		return tagRepository.findById(id)
				.orElseThrow(() -> new TagNotFoundException(id));
	}

	private void applyRequest(Tag tag, TagRequest request) {
		tag.setSlug(request.slug());
		tag.setName(request.name());
	}

}
