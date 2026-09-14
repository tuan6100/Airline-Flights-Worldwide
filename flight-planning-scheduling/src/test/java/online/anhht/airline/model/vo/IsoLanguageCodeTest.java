package online.anhht.airline.model.vo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IsoLanguageCodeTest {

    @Test
    void findByCode() {
        assertEquals(IsoLanguageCode.ENGLISH, IsoLanguageCode.findByCode("en"));
        assertEquals(IsoLanguageCode.RUSSIAN, IsoLanguageCode.findByCode("ru"));
        assertThrows(IllegalArgumentException.class, () -> IsoLanguageCode.findByCode("fr"));
        assertThrows(IllegalArgumentException.class, () -> IsoLanguageCode.findByCode("fr"));
    }
}