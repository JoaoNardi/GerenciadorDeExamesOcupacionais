package com.joaonardi.gerenciadorocupacional.model;

public enum Operador {
    IGUAL("=="),
    MAIOR(">"),
    MENOR("<"),
    MAIOR_OU_IGUAL(">="),
    MENOR_OU_IGUAL("<="),
    DIFERENTE("!="),
    CONTEM("<>");
    private final String operador;

    Operador(String operador) {
        this.operador = operador;
    }
    public String getOperador() {
        return operador;
    }
    public static Operador from(String operador) {
        for (Operador p : Operador.values()) {
            if (p.getOperador() == operador) {
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
            case MAIOR:
                return "Maior";
            case MENOR:
                return "Menor";
            case MAIOR_OU_IGUAL:
                return "Maior ou igual";
            case MENOR_OU_IGUAL:
                return "Menor ou igual";
            case DIFERENTE:
                return "Diferente";
            case CONTEM:
                return "Contêm";
            default:
                return "Desconhecido";
        }
    }
}
