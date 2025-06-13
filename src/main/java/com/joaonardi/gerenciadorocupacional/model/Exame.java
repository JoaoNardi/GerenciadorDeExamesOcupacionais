package com.joaonardi.gerenciadorocupacional.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.LocalDate;


@Entity
public class Exame {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Getter
    private String tipo;
    @Getter
    private Integer dataValidade;

    public Exame(){}

    public static final class ExameBuilder {
        private Integer id;
        private String tipo;
        private Integer dataValidade;

        private ExameBuilder() {
        }

        public static ExameBuilder builder() {
            return new ExameBuilder();
        }

        public ExameBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public ExameBuilder tipo(String tipo) {
            this.tipo = tipo;
            return this;
        }

        public ExameBuilder dataValidade(Integer dataValidade) {
            this.dataValidade = dataValidade;
            return this;
        }

        public Exame build() {
            Exame exame = new Exame();
            exame.tipo = this.tipo;
            exame.dataValidade = this.dataValidade;
            exame.id = this.id;
            return exame;
        }
    }

    //builder

}
