package online.anhht.airline.planning.adapter.outbound.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.UserType;
import online.anhht.airline.model.vo.MapCoordinates;
import online.anhht.airline.model.vo.IsoLanguageCode;
import online.anhht.airline.model.Airport;
import online.anhht.airline.model.Coordinates;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Table(name = "airports_data", schema = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AirportEntity {

    @Id
    @Column(name = "airport_code", columnDefinition = "char(3)")
    private String id;

    @Column(name = "airport_name")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<IsoLanguageCode, String> name;

    @Column(name = "city")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<IsoLanguageCode, String> city;

    @Column(name = "country")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<IsoLanguageCode, String> country;

    @Column(name = "coordinates", columnDefinition = "point")
    @Type(CoordinatesType.class)
    private MapCoordinates coordinates;

    @Column(name = "timezone")
    private TimeZone timeZone;

    public Airport toDomain() {
        String airportName = name != null ? name.getOrDefault(IsoLanguageCode.ENGLISH, id) : id;
        String cityName = city != null ? city.getOrDefault(IsoLanguageCode.ENGLISH, "Unknown") : "Unknown";
        String countryName = country != null ? country.getOrDefault(IsoLanguageCode.ENGLISH, "Unknown") : "Unknown";
        Coordinates coords = coordinates != null ? new Coordinates(coordinates.getLatitude(), coordinates.getLongitude()) : new Coordinates(0, 0);
        ZoneId zone = timeZone != null ? timeZone.toZoneId() : ZoneId.of("UTC");
        return Airport.of(id, airportName, cityName, countryName, coords, zone, 100);
    }

    private static class CoordinatesType implements UserType<MapCoordinates> {

        private static final Pattern POINT_PATTERN = Pattern.compile(
                "(?:POINT\\s*)?\\(\\s*([+-]?\\d+(?:\\.\\d+)?)[\\s,]+([+-]?\\d+(?:\\.\\d+)?)\\s*\\)",
                Pattern.CASE_INSENSITIVE
        );

        @Override
        public int getSqlType() {
            return Types.OTHER;
        }

        @Override
        public Class<MapCoordinates> returnedClass() {
            return MapCoordinates.class;
        }

        @Override
        public boolean equals(MapCoordinates x, MapCoordinates y) {
            return UserType.super.equals(x, y);
        }

        @Override
        public int hashCode(MapCoordinates x) {
            return UserType.super.hashCode(x);
        }

        @Override
        public MapCoordinates nullSafeGet(ResultSet rs, int position, WrapperOptions options) throws SQLException {
            Object obj = rs.getObject(position);
            if (obj == null) {
                return null;
            }
            String str = obj.toString();
            Matcher matcher = POINT_PATTERN.matcher(str);
            if (matcher.find()) {
                double lon = Double.parseDouble(matcher.group(1));
                double lat = Double.parseDouble(matcher.group(2));
                return new MapCoordinates(lon, lat);
            }
            return null;
        }

        @Override
        public void nullSafeSet(PreparedStatement st, MapCoordinates value, int index, WrapperOptions options) throws SQLException {
            if (value == null) {
                st.setNull(index, Types.OTHER);
            } else {
                st.setObject(index, String.format(Locale.ROOT, "(%f, %f)", value.getLongitude(), value.getLatitude()), Types.OTHER);
            }
        }

        @Override
        public MapCoordinates deepCopy(MapCoordinates value) {
            return value;
        }

        @Override
        public boolean isMutable() {
            return false;
        }

        @Override
        public Serializable disassemble(MapCoordinates value) {
            return value;
        }

        @Override
        public MapCoordinates assemble(Serializable cached, Object owner) {
            return (MapCoordinates) cached;
        }
    }
}
