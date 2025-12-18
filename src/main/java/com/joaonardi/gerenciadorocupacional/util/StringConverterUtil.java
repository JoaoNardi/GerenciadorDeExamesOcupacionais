package com.joaonardi.gerenciadorocupacional.util;

import javafx.util.StringConverter;

import java.util.List;
import java.util.function.Function;

public class StringConverterUtil {
    public static <T> StringConverter<T> choice(
            Iterable<T> items,
            List<Function<T, String>> toStringNome
    ) {
        return new StringConverter<T>() {
            @Override
            public String toString(T t) {
                if (toStringNome.isEmpty()) {
                    return null;
                }
                String retorno = "";
                for (Function<T, String> function : toStringNome) {
                    if (toStringNome.getLast() != function){
                        retorno += t != null ? function.apply(t) + " - " : "";
                    } else {
                        retorno += t != null ? function.apply(t) : "";
                    }
                }
                return retorno;
            }

            @Override
            public T fromString(String s) {
                if (s == null || s.isBlank()) {
                    return null;
                }
                for (T t : items) {
                    String texto = toString(t);
                    if (texto.equalsIgnoreCase(s)) {
                        return t;
                    }
                }
                return null;
            }
        };
    }
}