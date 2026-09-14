package online.anhht.airline.model.vo;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

public final class MapCoordinates implements Serializable {


    @Serial
    private static final long serialVersionUID = 0L;

    @Getter
    private final double longitude;

    @Getter
    private final double latitude;

    @Getter
    private final double phi;

    @Getter
    private final double theta;

    public MapCoordinates(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.phi = Math.toRadians(latitude);
        this.theta = Math.toRadians(longitude);
    }

}
