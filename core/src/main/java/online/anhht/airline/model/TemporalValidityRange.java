package online.anhht.airline.model;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Represents a temporal validity range (equivalent to PostgreSQL tstzrange).
 * Used for monthly/seasonal route map lifecycle management.
 */
public record TemporalValidityRange(OffsetDateTime validFrom, OffsetDateTime validTo) {

    public TemporalValidityRange {
        Objects.requireNonNull(validFrom, "validFrom must not be null");
        Objects.requireNonNull(validTo, "validTo must not be null");
        if (validTo.isBefore(validFrom)) {
            throw new IllegalArgumentException("validTo must be on or after validFrom");
        }
    }

    /**
     * Checks if a given timestamp falls within this validity range (inclusive of boundaries).
     */
    public boolean contains(OffsetDateTime timestamp) {
        if (timestamp == null) {
            return false;
        }
        return !timestamp.isBefore(validFrom) && !timestamp.isAfter(validTo);
    }

    /**
     * Checks if this validity range overlaps with another validity range.
     */
    public boolean overlaps(TemporalValidityRange other) {
        if (other == null) {
            return false;
        }
        return !this.validTo.isBefore(other.validFrom) && !other.validTo.isBefore(this.validFrom);
    }
}
