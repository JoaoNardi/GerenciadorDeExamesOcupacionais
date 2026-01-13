package com.joaonardi.gerenciadorocupacional.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class FormataData {

    private FormataData() {}

    public static final DateTimeFormatter ISO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter BR =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String iso(LocalDate data) {
        return data != null ? data.format(ISO) : null;
    }

    public static String br(LocalDate data) {
        return data != null ? data.format(BR) : null;
    }
}
