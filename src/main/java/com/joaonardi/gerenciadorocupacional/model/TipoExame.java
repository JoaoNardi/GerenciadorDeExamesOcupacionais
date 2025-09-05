package com.joaonardi.gerenciadorocupacional.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
public class TipoExame {
    @Setter
    private Integer id;
    private String nome;
    private Integer periodicidade;


    public static final class TipoExameBuilder {
        private Integer id;
        private String nome;
        private Integer periodicidade;

        private TipoExameBuilder() {
        }

        public static TipoExameBuilder builder() {
            return new TipoExameBuilder();
        }

        public TipoExameBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public TipoExameBuilder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public TipoExameBuilder periodicidade(Integer periodicidade) {
            this.periodicidade = periodicidade;
            return this;
        }

        public TipoExame build() {
            TipoExame tipoExame = new TipoExame();
            tipoExame.id = this.id;
            tipoExame.nome = this.nome;
            tipoExame.periodicidade = this.periodicidade;
            return tipoExame;
        }
    }
}
