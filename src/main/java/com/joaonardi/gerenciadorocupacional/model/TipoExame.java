package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class TipoExame extends TipoDe{

    public static final class TipoExameBuilder {
        private Integer id;
        private String nome;

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

        public TipoExame build() {
            TipoExame tipoExame = new TipoExame();
            tipoExame.setId(id);
            tipoExame.nome = this.nome;
            return tipoExame;
        }
    }
}
