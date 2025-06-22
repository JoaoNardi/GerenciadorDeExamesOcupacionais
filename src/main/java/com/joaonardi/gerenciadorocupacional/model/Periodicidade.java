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