package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class Condicao {
    private Integer id;
    private Conjunto conjunto;
    private String referencia;
    private String operador;
    private String parametro;

    public static final class CondicaoBuilder {
        private Integer id;
        private Conjunto conjunto;
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

        public CondicaoBuilder conjunto(Conjunto conjunto) {
            this.conjunto = conjunto;
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
            condicao.id = this.id;
            condicao.conjunto = this.conjunto;
            condicao.operador = this.operador;
            condicao.referencia = this.referencia;
            condicao.parametro = this.parametro;
            return condicao;
        }
    }
}
