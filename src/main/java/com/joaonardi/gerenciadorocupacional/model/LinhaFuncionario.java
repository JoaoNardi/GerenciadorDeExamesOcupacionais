package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class LinhaFuncionario {
    @Getter
    private final Funcionario funcionario;
    private final List<Tipo> tipos;

    public LinhaFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
        this.tipos = new ArrayList<>();
    }

    public void addTipo(Tipo tipo) {
        this.tipos.add(tipo);
    }

    public Tipo getTipoPor(TipoDe tipoDe) {
        return tipos.stream()
                .filter(t -> {
                    if (t instanceof Exame e) return e.getTipoExame().equals(tipoDe);
                    if (t instanceof Certificado c) return c.getTipoCertificado().equals(tipoDe);
                    return false;
                })
                .findFirst()
                .orElse(null);
    }
}