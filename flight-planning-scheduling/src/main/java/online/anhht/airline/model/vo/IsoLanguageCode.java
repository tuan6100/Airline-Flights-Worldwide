package online.anhht.airline.model.vo;

import com.fasterxml.jackson.annotation.JsonValue;

import java.text.MessageFormat;
import java.util.Objects;

public enum IsoLanguageCode {

    ENGLISH("en"),

    RUSSIAN("ru");

    private final String code;

    IsoLanguageCode(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public static IsoLanguageCode findByCode(String code) {
        for (IsoLanguageCode lang: IsoLanguageCode.values()) {
            if (Objects.equals(lang.code, code)) {
                return lang;
            }
        }
        throw new IllegalArgumentException(MessageFormat.format("Unknown language code: {0}", code));
    }
}
