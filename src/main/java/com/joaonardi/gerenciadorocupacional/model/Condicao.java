package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class Condicao {
    private Integer id;
    private Integer tipoExameId;
    private String referencia; //idade, setor, enfermidade
    private String operador; // ==;>=;!= etc
    private String parametro; // idade 45; setor Escritrorio
    private Integer periodicidade;


    public static final class CondicaoBuilder {
        private Integer id;
        private Integer tipoExameId;
        private String referencia;
        private String operador;
        private String parametro;
        private Integer periodicidade;

        private CondicaoBuilder() {
        }

        public static CondicaoBuilder builder() {
            return new CondicaoBuilder();
        }

        public CondicaoBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public CondicaoBuilder tipoExameId(Integer tipoExameId) {
            this.tipoExameId = tipoExameId;
            return this;
        }

        public CondicaoBuilder referencia(String referencia) {
            this.referencia = referencia;
            return this;
        }

        public CondicaoBuilder operador(String operador) {
            this.operador = operador;
            return this;
        }

        public CondicaoBuilder parametro(String parametro) {
            this.parametro = parametro;
            return this;
        }

        public CondicaoBuilder periodicidade(Integer periodicidade) {
            this.periodicidade = periodicidade;
            return this;
        }

        public Condicao build() {
            Condicao condicao = new Condicao();
            condicao.referencia = this.referencia;
            condicao.parametro = this.parametro;
            condicao.id = this.id;
            condicao.tipoExameId = this.tipoExameId;
            condicao.operador = this.operador;
            condicao.periodicidade = this.periodicidade;
            return condicao;
        }
    }
}
