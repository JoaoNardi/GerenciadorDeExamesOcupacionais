package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;

@Getter
public enum Periodicidade {
    SEM_PERIODICIDADE(0),
    BIMESTRAL(2),
    TRIMESTRAL(3),
    SEMESTRAL(6),
    ANUAL(12),
    BIENAL(24),
    TRIENAL(36);

    private final int valor;

    Periodicidade(int valor) {
        this.valor = valor;
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
        return switch (this) {
            case SEM_PERIODICIDADE -> "Sem Periodicidade";
            case BIMESTRAL -> "Bimestral";
            case TRIMESTRAL -> "Trimestral";
            case SEMESTRAL -> "Semestral";
            case ANUAL -> "Anual";
            case BIENAL -> "Bienal";
            case TRIENAL -> "Trienal";
        };
    }
}