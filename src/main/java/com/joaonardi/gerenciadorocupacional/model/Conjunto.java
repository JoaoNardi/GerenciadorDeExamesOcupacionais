package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
public class Conjunto {
    @Setter
    private Integer id;
    private Integer tipoExameId;
    private Integer periodicidade;

    public static final class ConjuntoBuilder {
        private Integer id;
        private Integer tipoExameId;
        private Integer periodicidade;

        private ConjuntoBuilder() {
        }

        public static ConjuntoBuilder builder() {
            return new ConjuntoBuilder();
        }

        public ConjuntoBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public ConjuntoBuilder tipoExameId(Integer tipoExameId) {
            this.tipoExameId = tipoExameId;
            return this;
        }

        public ConjuntoBuilder periodicidade(Integer periodicidade) {
            this.periodicidade = periodicidade;
            return this;
        }

        public Conjunto build() {
            Conjunto conjunto = new Conjunto();
            conjunto.setId(id);
            conjunto.tipoExameId = this.tipoExameId;
            conjunto.periodicidade = this.periodicidade;
            return conjunto;
        }
    }
}
