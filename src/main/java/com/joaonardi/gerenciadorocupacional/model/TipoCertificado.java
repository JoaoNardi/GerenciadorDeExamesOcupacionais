package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class TipoCertificado extends TipoDe{
    protected Integer periodicidade;

    public static final class TipoCertificadoBuilder {
        private Integer id;
        private String nome;
        private Integer periodicidade;

        private TipoCertificadoBuilder() {
        }

        public static TipoCertificadoBuilder builder() {
            return new TipoCertificadoBuilder();
        }

        public TipoCertificadoBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public TipoCertificadoBuilder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public TipoCertificadoBuilder periodicidade(Integer periodicidade) {
            this.periodicidade = periodicidade;
            return this;
        }

        public TipoCertificado build() {
            TipoCertificado tipoCertificado = new TipoCertificado();
            tipoCertificado.id = this.id;
            tipoCertificado.periodicidade = this.periodicidade;
            tipoCertificado.nome = this.nome;
            return tipoCertificado;
        }
    }
}
