package online.anhht.airline.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import online.anhht.airline.model.vo.IsoLanguageCode;
import online.anhht.airline.model.vo.MapCoordinates;
import online.anhht.airline.planning.adapter.outbound.jpa.AirportEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Sql({"classpath:db/schema-h2.sql", "classpath:db/init-airports.sql"})
class AirportTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    public void insert() {
        AirportEntity newAirport = new AirportEntity(
                "BBB",
                Map.of(IsoLanguageCode.ENGLISH, "Test airport"),
                Map.of(IsoLanguageCode.ENGLISH, "Test airport"),
                Map.of(IsoLanguageCode.ENGLISH, "Test airport"),
                new MapCoordinates(90, 90),
                TimeZone.getDefault()
        );
        entityManager.persist(newAirport);
        entityManager.flush();

        AirportEntity persistedAirport = entityManager.createQuery(
                "SELECT ap FROM AirportEntity ap WHERE ap.id = :id",
                AirportEntity.class
        ).setParameter("id", "BBB").getSingleResult();

        var name = persistedAirport.getName();
        assertThat(name)
                .hasSize(1)
                .containsEntry(IsoLanguageCode.ENGLISH, "Test airport");

        var coordinates = persistedAirport.getCoordinates();
        assertThat(coordinates).usingRecursiveComparison().isEqualTo(new MapCoordinates(90,90));

        var timeZone = persistedAirport.getTimeZone();
        var zoneId = timeZone.toZoneId();
        assertThat(zoneId.getRules().getOffset(LocalDateTime.now()).getId()).containsIgnoringCase("7:00");
    }

    @Test
    @Transactional(readOnly = true)
    public void shouldReturnAirport() {
        AirportEntity airport = entityManager.createQuery(
                "SELECT ap FROM AirportEntity ap WHERE ap.id = :id",
                AirportEntity.class
        ).setParameter("id", "AAA").getSingleResult();

        var name = airport.getName();
        assertThat(name)
                .hasSize(2)
                .containsEntry(IsoLanguageCode.ENGLISH, "Anaa")
                .containsEntry(IsoLanguageCode.RUSSIAN, "Анаа");

        var country = airport.getCountry();
        assertThat(country)
                .hasSize(2)
                .containsEntry(IsoLanguageCode.ENGLISH, "French Polynesia")
                .containsEntry(IsoLanguageCode.RUSSIAN, "Французская Полинезия");

        var coordinates = airport.getCoordinates();
        assertThat(coordinates).usingRecursiveComparison().isEqualTo(new MapCoordinates(-145.51,-17.3526));

        var timeZone = airport.getTimeZone();
        var zoneId = timeZone.toZoneId();
        assertThat(zoneId.getRules().getOffset(LocalDateTime.now()).getId()).isEqualTo("-10:00");
    }

}