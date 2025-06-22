package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;

import java.time.LocalDate;
@Getter
public class Certificado {
    private Integer id;
    private Integer tipoCertificadoId;
    private Integer funcionarioId;
    private LocalDate dataEmissao;
    private LocalDate dataValidade;

    public static final class CertificadoBuilder {
        private Integer id;
        private Integer tipoCertificadoId;
        private Integer funcionarioId;
        private LocalDate dataEmissao;
        private LocalDate dataValidade;

        private CertificadoBuilder() {
        }

        public static CertificadoBuilder builder() {
            return new CertificadoBuilder();
        }

        public CertificadoBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public CertificadoBuilder tipoCertificadoId(Integer tipoCertificadoId) {
            this.tipoCertificadoId = tipoCertificadoId;
            return this;
        }

        public CertificadoBuilder funcionarioId(Integer funcionarioId) {
            this.funcionarioId = funcionarioId;
            return this;
        }

        public CertificadoBuilder dataEmissao(LocalDate dataEmissao) {
            this.dataEmissao = dataEmissao;
            return this;
        }

        public CertificadoBuilder dataValidade(LocalDate dataValidade) {
            this.dataValidade = dataValidade;
            return this;
        }

        public Certificado build() {
            Certificado certificado = new Certificado();
            certificado.tipoCertificadoId = this.tipoCertificadoId;
            certificado.id = this.id;
            certificado.dataEmissao = this.dataEmissao;
            certificado.dataValidade = this.dataValidade;
            certificado.funcionarioId = this.funcionarioId;
            return certificado;
        }
    }
}
