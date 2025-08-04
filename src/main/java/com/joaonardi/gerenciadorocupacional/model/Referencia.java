package com.joaonardi.gerenciadorocupacional.model;

public enum Referencia {
    IDADE("Idade"),
    SETOR("Setor"),
    ENFERMIDADE("Enfermidade");

    private final String label;

    Referencia(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
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
        switch (this) {
            case IDADE:
                return "Idade";
            case SETOR:
                return "Setor";
            case ENFERMIDADE:
                return "Enfermidade";
            default:
                return "Sem Referencia";
        }
    }
}
