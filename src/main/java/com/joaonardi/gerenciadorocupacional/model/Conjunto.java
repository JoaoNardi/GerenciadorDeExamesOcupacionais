package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
public class Conjunto {
    @Setter
    private Integer id;
    private TipoExame tipoExame;
    private Integer periodicidade;

    public static final class ConjuntoBuilder {
        private Integer id;
        private TipoExame tipoExame;
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

        public ConjuntoBuilder tipoExame(TipoExame tipoExame) {
            this.tipoExame = tipoExame;
            return this;
        }

        public ConjuntoBuilder periodicidade(Integer periodicidade) {
            this.periodicidade = periodicidade;
            return this;
        }

        public Conjunto build() {
            Conjunto conjunto = new Conjunto();
            conjunto.setId(id);
            conjunto.tipoExame = this.tipoExame;
            conjunto.periodicidade = this.periodicidade;
            return conjunto;
        }
    }
}
