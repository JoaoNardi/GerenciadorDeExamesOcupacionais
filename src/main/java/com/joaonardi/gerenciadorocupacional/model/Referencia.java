package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;

@Getter
public enum Referencia {
    IDADE("Idade"),
    SETOR("Setor"),
    ENFERMIDADE("Enfermidade");

    private final String label;

    Referencia(String label) {
        this.label = label;
    }

    public static Referencia from(String label) {
        for (Referencia r : values()) {
            if (r.getLabel().equalsIgnoreCase(label)) {
                return r;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return switch (this) {
            case IDADE -> "Idade";
            case SETOR -> "Setor";
            case ENFERMIDADE -> "Enfermidade";
        };
    }
}
