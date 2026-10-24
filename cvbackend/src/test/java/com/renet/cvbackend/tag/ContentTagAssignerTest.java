package com.renet.cvbackend.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContentTagAssignerTest {

	@Mock
	private TagRepository tagRepository;

	@InjectMocks
	private ContentTagAssigner contentTagAssigner;

	@Test
	void replaceTags_addsResolvedTagsAndDeduplicates() {
		var java = tag(1L, "java", "Java");
		var spring = tag(2L, "spring", "Spring");
		when(tagRepository.findById(1L)).thenReturn(Optional.of(java));
		when(tagRepository.findById(2L)).thenReturn(Optional.of(spring));

		var target = new ArrayList<Tag>();
		contentTagAssigner.replaceTags(target, List.of(1L, 2L, 1L));

		assertEquals(List.of(java, spring), target);
	}

	@Test
	void replaceTags_withNullOrEmpty_clearsTarget() {
		var target = new ArrayList<Tag>();
		target.add(tag(1L, "java", "Java"));

		contentTagAssigner.replaceTags(target, null);
		assertTrue(target.isEmpty());

		target.add(tag(1L, "java", "Java"));
		contentTagAssigner.replaceTags(target, List.of());
		assertTrue(target.isEmpty());
	}

	@Test
	void replaceTags_withMissingTag_throwsContentTagNotFound() {
		when(tagRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(
				ContentTagNotFoundException.class,
				() -> contentTagAssigner.replaceTags(new ArrayList<>(), List.of(999L))
		);
	}

	private static Tag tag(Long id, String slug, String name) {
		var tag = new Tag();
		tag.setId(id);
		tag.setSlug(slug);
		tag.setName(name);
		return tag;
	}

}
