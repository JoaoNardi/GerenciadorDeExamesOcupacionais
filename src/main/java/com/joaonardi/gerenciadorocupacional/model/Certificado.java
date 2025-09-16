package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;

import java.time.LocalDate;
@Getter
public class Certificado {
    private Integer id;
    private Integer idTipoCertificado;
    private Integer funcionarioId;
    private LocalDate dataEmissao;
    private LocalDate dataValidade;
    private Integer atualizadoPor;

    public static final class CertificadoBuilder {
        private Integer id;
        private Integer idTipoCertificado;
        private Integer funcionarioId;
        private LocalDate dataEmissao;
        private LocalDate dataValidade;
        private Integer atualizadoPor;

        private CertificadoBuilder() {
        }

        public static CertificadoBuilder builder() {
            return new CertificadoBuilder();
        }

        public CertificadoBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public CertificadoBuilder idTipoCertificado(Integer idTipoCertificado) {
            this.idTipoCertificado = idTipoCertificado;
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

        public CertificadoBuilder atualizadoPor(Integer atualizadoPor) {
            this.atualizadoPor = atualizadoPor;
            return this;
        }

        public Certificado build() {
            Certificado certificado = new Certificado();
            certificado.dataValidade = this.dataValidade;
            certificado.dataEmissao = this.dataEmissao;
            certificado.funcionarioId = this.funcionarioId;
            certificado.id = this.id;
            certificado.atualizadoPor = this.atualizadoPor;
            certificado.idTipoCertificado = this.idTipoCertificado;
            return certificado;
        }
    }
}
