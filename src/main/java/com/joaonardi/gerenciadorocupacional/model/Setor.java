package com.joaonardi.gerenciadorocupacional.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;


@Entity
public class Setor {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Getter
    private String area;
    @Getter
    private Integer grauRisco;

    public Setor(){}

    //builder
    public static final class SetorBuilder {
        private Integer id;
        private String area;
        private Integer grauRisco;

        private SetorBuilder() {
        }

        public static SetorBuilder builder() {
            return new SetorBuilder();
        }

        public SetorBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public SetorBuilder area(String area) {
            this.area = area;
            return this;
        }

        public SetorBuilder grauRisco(Integer grauRisco) {
            this.grauRisco = grauRisco;
            return this;
        }

        public Setor build() {
            Setor setor = new Setor();
            setor.area = this.area;
            setor.id = this.id;
            setor.grauRisco = this.grauRisco;
            return setor;
        }
    }
}
