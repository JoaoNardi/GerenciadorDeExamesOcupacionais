package com.joaonardi.gerenciadorocupacional.backend.model;

public class Setor {
    private Integer id;
    private String nome;
    private Integer grauRisco;

    //getters
    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Integer getGrauRisco() {
        return grauRisco;
    }

    //builder
    public static final class SetorBuilder {
        private Integer id;
        private String nome;
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

        public SetorBuilder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public SetorBuilder grauRisco(Integer grauRisco) {
            this.grauRisco = grauRisco;
            return this;
        }

        public Setor build() {
            Setor setor = new Setor();
            setor.nome = this.nome;
            setor.id = this.id;
            setor.grauRisco = this.grauRisco;
            return setor;
        }
    }
}
