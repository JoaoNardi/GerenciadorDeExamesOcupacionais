package com.joaonardi.gerenciadorocupacional.model;

public enum GrauDeRisco {
    SEM_RISCO(0),
    BAIXO_RISCO(1),
    MEDIO_RISCO(2),
    ALTO_RISCO(3);

    private final int valor;

    GrauDeRisco(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    @Override
    public String toString() {
        switch (this) {
            case SEM_RISCO:
                return "Sem Risco";
            case BAIXO_RISCO:
                return "Baixo Risco";
            case MEDIO_RISCO:
                return "Médio Risco";
            case ALTO_RISCO:
                return "Alto Risco";
            default:
                return "Desconhecido";
        }
    }
}
