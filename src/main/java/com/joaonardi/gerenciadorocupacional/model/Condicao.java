package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class Condicao {
    private Integer id;
    private Integer conjuntoId;
    private String referencia; //idade, setor, enfermidade
    private String operador; // ==;>=;!= etc
    private String parametro; // idade 45; setor Escritrorio


    public static final class CondicaoBuilder {
        private Integer id;
        private Integer conjuntoId;
        private String referencia;
        private String operador;
        private String parametro;

        private CondicaoBuilder() {
        }

        public static CondicaoBuilder builder() {
            return new CondicaoBuilder();
        }

        public CondicaoBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public CondicaoBuilder conjuntoId(Integer conjuntoId) {
            this.conjuntoId = conjuntoId;
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

        public Condicao build() {
            Condicao condicao = new Condicao();
            condicao.conjuntoId = this.conjuntoId;
            condicao.operador = this.operador;
            condicao.id = this.id;
            condicao.referencia = this.referencia;
            condicao.parametro = this.parametro;
            return condicao;
        }
    }
}
