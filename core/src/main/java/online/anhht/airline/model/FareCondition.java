package online.anhht.airline.model;

/**
 * Service travel class segmentation / fare conditions.
 */
public enum FareCondition {
    ECONOMY("Economy"),
    COMFORT("Comfort"), // Premium Economy
    BUSINESS("Business");

    private final String displayName;

    FareCondition(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
