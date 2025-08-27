package com.joaonardi.gerenciadorocupacional.model;

import java.util.Objects;

public enum Operador {
    IGUAL("=="),
    MAIOR_OU_IGUAL(">="),
    MENOR_OU_IGUAL("<="),
    DIFERENTE("!=");
    private final String operador;

    Operador(String operador) {
        this.operador = operador;
    }

    public String getOperador() {
        return operador;
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
        switch (this) {
            case IGUAL:
                return "Igual";
            case MAIOR_OU_IGUAL:
                return "Maior ou igual";
            case MENOR_OU_IGUAL:
                return "Menor ou igual";
            case DIFERENTE:
                return "Diferente";
            default:
                return "Desconhecido";
        }
    }
}
