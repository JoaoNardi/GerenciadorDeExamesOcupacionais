package com.joaonardi.gerenciadorocupacional.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
@Getter
public class TipoDe {
    @Setter
    protected Integer id;
    protected String nome;
}
