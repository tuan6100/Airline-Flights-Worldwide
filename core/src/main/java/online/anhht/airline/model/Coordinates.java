package online.anhht.airline.model;

import java.util.Objects;

/**
 * Geographic coordinates (latitude and longitude) with distance calculation.
 */
public record Coordinates(double latitude, double longitude) {

    private static final double EARTH_RADIUS_KM = 6371.0;

    public Coordinates {
        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
        }
    }

    /**
     * Calculates the great-circle distance between two geographic coordinates in kilometers
     * using the Haversine formula.
     */
    public double distanceTo(Coordinates other) {
        Objects.requireNonNull(other, "Other coordinates must not be null");
        double lat1Rad = Math.toRadians(this.latitude);
        double lat2Rad = Math.toRadians(other.latitude);
        double deltaLat = Math.toRadians(other.latitude - this.latitude);
        double deltaLon = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
