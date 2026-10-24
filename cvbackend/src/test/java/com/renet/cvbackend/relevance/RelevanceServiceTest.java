package com.renet.cvbackend.relevance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.renet.cvbackend.tag.Tag;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RelevanceServiceTest {

	private RelevanceService relevanceService;

	@BeforeEach
	void setUp() {
		relevanceService = new RelevanceService();
	}

	@Test
	void score_sumsMatchingWeights() {
		var weights = Map.of(
				1L, new BigDecimal("1.000"),
				2L, new BigDecimal("0.500"),
				3L, new BigDecimal("0.200")
		);

		BigDecimal score = relevanceService.score(
				List.of(tag(1L), tag(2L), tag(99L)),
				weights
		);

		assertEquals(new BigDecimal("1.500"), score);
	}

	@Test
	void score_withNoOverlap_returnsZero() {
		assertEquals(
				BigDecimal.ZERO,
				relevanceService.score(List.of(tag(9L)), Map.of(1L, BigDecimal.ONE))
		);
	}

	@Test
	void score_withEmptyTagsOrWeights_returnsZero() {
		assertEquals(BigDecimal.ZERO, relevanceService.score(List.of(), Map.of(1L, BigDecimal.ONE)));
		assertEquals(BigDecimal.ZERO, relevanceService.score(List.of(tag(1L)), Map.of()));
		assertEquals(BigDecimal.ZERO, relevanceService.score(null, Map.of(1L, BigDecimal.ONE)));
	}

	@Test
	void max_returnsHighestScore() {
		assertEquals(
				new BigDecimal("2.000"),
				relevanceService.max(List.of(
						new BigDecimal("0.500"),
						new BigDecimal("2.000"),
						new BigDecimal("1.000")
				))
		);
		assertEquals(BigDecimal.ZERO, relevanceService.max(List.of()));
	}

	@Test
	void sortByScoreDesc_ordersByScoreThenId() {
		record Item(long id, BigDecimal score) {
		}

		List<Item> sorted = relevanceService.sortByScoreDesc(
				List.of(
						new Item(2L, new BigDecimal("1.000")),
						new Item(1L, new BigDecimal("2.000")),
						new Item(3L, new BigDecimal("1.000"))
				),
				Item::score,
				Item::id
		);

		assertEquals(List.of(1L, 2L, 3L), sorted.stream().map(Item::id).toList());
	}

	@Test
	void highlighted_whenScorePositive() {
		assertTrue(relevanceService.highlighted(new BigDecimal("0.001")));
		assertFalse(relevanceService.highlighted(BigDecimal.ZERO));
		assertFalse(relevanceService.highlighted(null));
	}

	private static Tag tag(Long id) {
		var tag = new Tag();
		tag.setId(id);
		tag.setSlug("t-" + id);
		tag.setName("T" + id);
		return tag;
	}

}
