package online.anhht.airline.util;

import online.anhht.airline.model.vo.MapCoordinates;

public final class Distance {

    private final double centralAngle;

    public Distance(MapCoordinates p1, MapCoordinates p2) {
        if (p1 == null || p2 == null) {
            throw new IllegalArgumentException("These points must not be null");
        }

        double phi1 = p1.getPhi();
        double phi2 = p2.getPhi();
        double dPhi = phi1 - phi2;
        double dTheta = p1.getTheta() - p2.getTheta();
        double a = Math.pow(Math.sin(dPhi / 2), 2) +
                Math.cos(phi1) * Math.cos(phi2) * Math.pow(Math.sin(dTheta / 2), 2);
        this.centralAngle = 2 * Math.asin(Math.sqrt(a));
    }

    private static final double EARTH_RADIUS = 6371.0 * 1e3;

    public double straightDistance() {
        return 2 * EARTH_RADIUS * Math.sin(centralAngle / 2);
    }

    public double surfaceDistance() {
        return EARTH_RADIUS * centralAngle;
    }

}
