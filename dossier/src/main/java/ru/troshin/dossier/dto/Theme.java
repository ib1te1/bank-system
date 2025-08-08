package ru.troshin.dossier.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Theme {

    FINISH_REGISTRATION("FINISH_REGISTRATION"),

    CREATE_DOCUMENTS("CREATE_DOCUMENTS"),

    SEND_DOCUMENTS("SEND_DOCUMENTS"),

    SEND_SES("SEND_SES"),

    CREDIT_ISSUED("CREDIT_ISSUED"),

    STATEMENT_DENIED("STATEMENT_DENIED");

    private String value;

    Theme(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static Theme fromValue(String value) {
        for (Theme b : Theme.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
