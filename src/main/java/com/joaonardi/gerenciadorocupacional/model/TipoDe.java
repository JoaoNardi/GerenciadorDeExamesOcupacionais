package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class TipoDe {
    @Setter
    protected Integer id;
    protected String nome;
    protected Integer periodicidade;
}
