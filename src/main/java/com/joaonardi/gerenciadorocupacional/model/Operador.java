package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum Operador {
    IGUAL("=="),
    MAIOR_OU_IGUAL(">="),
    MENOR_OU_IGUAL("<="),
    DIFERENTE("!=");
    private final String operador;

    Operador(String operador) {
        this.operador = operador;
    }

    public static Operador from(String operador) {
        for (Operador p : Operador.values()) {
            if (Objects.equals(p.getOperador(), operador)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return switch (this) {
            case IGUAL -> "Igual";
            case MAIOR_OU_IGUAL -> "Maior ou igual";
            case MENOR_OU_IGUAL -> "Menor ou igual";
            case DIFERENTE -> "Diferente";
        };
    }
}
