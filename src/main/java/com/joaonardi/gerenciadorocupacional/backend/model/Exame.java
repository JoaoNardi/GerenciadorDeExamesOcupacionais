package com.joaonardi.gerenciadorocupacional.backend.model;

public class Exame {
    private Integer id;
    private String tipo;

    //geters
    public Integer getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    //builder
    public static final class ExameBuilder {
        private Integer id;
        private String tipo;

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

        public Exame build() {
            Exame exame = new Exame();
            exame.id = this.id;
            exame.tipo = this.tipo;
            return exame;
        }
    }
}
