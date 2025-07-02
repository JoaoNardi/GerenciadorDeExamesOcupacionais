package com.joaonardi.gerenciadorocupacional.model;

public enum Periodicidade {
    SEM_PERIODICIDADE(0),
    SEMESTRAL(182),
    ANUAL(365);

    private final int valor;

    Periodicidade(int valor) {
        this.valor = valor;
    }
    public int getValor() {
        return valor;
    }
    public static Periodicidade fromValor(int valor) {
        for (Periodicidade p : Periodicidade.values()) {
            if (p.getValor() == valor) {
                return p;
            }
        }
        return null;
    }
    @Override
    public String toString() {
        switch (this) {
            case SEM_PERIODICIDADE:
                return "Sem Periodicidade";
            case SEMESTRAL:
                return "Semestral";
            case ANUAL:
                return "Anual";
            default:
                return "Desconhecido";
        }
    }
}