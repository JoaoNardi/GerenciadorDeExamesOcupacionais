package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
public class Tipo {
    @Setter
    protected Integer id;
    protected Funcionario funcionario;
    protected LocalDate dataEmissao;
    protected LocalDate dataValidade;
    protected Integer atualizadoPor;

    @Override
    public String toString() {
        return "Tipo{" +
                "id=" + id +
                ", funcionario=" + funcionario+
                ", dataEmissao=" + dataEmissao +
                ", dataValidade=" + dataValidade +
                ", atualizadoPor=" + atualizadoPor +
                '}';
    }
}
