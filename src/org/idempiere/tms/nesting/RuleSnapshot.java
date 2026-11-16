package org.idempiere.tms.nesting;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/** Immutable, provenance-qualified snapshot of one family's nesting rule. */
public record RuleSnapshot(String family, Map<String, Integer> ranks,
		NestingStructure structure, EvidenceState evidenceState, String sourceVersion) {

	public RuleSnapshot {
		family = requireText(family, "family");
		Objects.requireNonNull(ranks, "ranks");
		if (ranks.isEmpty()) throw new IllegalArgumentException("ranks must not be empty");
		TreeMap<String, Integer> normalized = new TreeMap<>();
		for (Map.Entry<String, Integer> entry : ranks.entrySet()) {
			String classification = requireText(entry.getKey(), "classification");
			Integer rank = Objects.requireNonNull(entry.getValue(), "rank");
			if (rank <= 0) throw new IllegalArgumentException("rank must be positive");
			normalized.put(classification, rank);
		}
		ranks = Collections.unmodifiableMap(normalized);
		Objects.requireNonNull(structure, "structure");
		Objects.requireNonNull(evidenceState, "evidenceState");
		sourceVersion = requireText(sourceVersion, "sourceVersion");
	}

	private static String requireText(String value, String field) {
		Objects.requireNonNull(value, field);
		String normalized = value.trim();
		if (normalized.isEmpty()) throw new IllegalArgumentException(field + " must not be blank");
		return normalized;
	}
}
