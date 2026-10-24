package com.renet.cvbackend.relevance;

import com.renet.cvbackend.tag.Tag;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import org.springframework.stereotype.Service;

@Service
public class RelevanceService {

	public BigDecimal score(Collection<Tag> tags, Map<Long, BigDecimal> weightsByTagId) {
		if (tags == null || tags.isEmpty() || weightsByTagId == null || weightsByTagId.isEmpty()) {
			return BigDecimal.ZERO;
		}

		BigDecimal total = BigDecimal.ZERO;
		for (Tag tag : tags) {
			if (tag == null || tag.getId() == null) {
				continue;
			}
			BigDecimal weight = weightsByTagId.get(tag.getId());
			if (weight != null) {
				total = total.add(weight);
			}
		}
		return total;
	}

	public BigDecimal max(Collection<BigDecimal> scores) {
		if (scores == null || scores.isEmpty()) {
			return BigDecimal.ZERO;
		}
		return scores.stream().max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
	}

	public <T> List<T> sortByScoreDesc(
			Collection<T> items,
			Function<T, BigDecimal> scoreFn,
			ToLongFunction<T> idFn
	) {
		return items.stream()
				.sorted(Comparator
						.comparing(scoreFn)
						.reversed()
						.thenComparingLong(idFn))
				.toList();
	}

	public boolean highlighted(BigDecimal score) {
		return score != null && score.compareTo(BigDecimal.ZERO) > 0;
	}

}
