package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class Certificado extends Tipo {
    private TipoCertificado tipoCertificado;

    public static final class CertificadoBuilder {
        private TipoCertificado tipoCertificado;
        private Integer id;
        private Funcionario funcionario;
        private LocalDate dataEmissao;
        private LocalDate dataValidade;
        private Integer atualizadoPor;

        private CertificadoBuilder() {
        }

        public static CertificadoBuilder builder() {
            return new CertificadoBuilder();
        }

        public CertificadoBuilder tipoCertificado(TipoCertificado tipoCertificado) {
            this.tipoCertificado = tipoCertificado;
            return this;
        }

        public CertificadoBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public CertificadoBuilder funcionario(Funcionario funcionario) {
            this.funcionario = funcionario;
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
            certificado.setId(id);
            certificado.dataEmissao = this.dataEmissao;
            certificado.funcionario = this.funcionario;
            certificado.atualizadoPor = this.atualizadoPor;
            certificado.tipoCertificado = this.tipoCertificado;
            certificado.dataValidade = this.dataValidade;
            return certificado;
        }
    }
}
