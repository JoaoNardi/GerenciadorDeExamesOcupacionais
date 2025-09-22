package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
public class Certificado extends Tipo {
    private Integer idTipoCertificado;


    public static final class CertificadoBuilder {
        private Integer idTipoCertificado;
        private Integer id;
        private Integer idFuncionario;
        private LocalDate dataEmissao;
        private LocalDate dataValidade;
        private Integer atualizadoPor;

        private CertificadoBuilder() {
        }

        @Override
        public String toString() {
            return "CertificadoBuilder{" +
                    "idTipoCertificado=" + idTipoCertificado +
                    ", id=" + id +
                    ", idFuncionario=" + idFuncionario +
                    ", dataEmissao=" + dataEmissao +
                    ", dataValidade=" + dataValidade +
                    ", atualizadoPor=" + atualizadoPor +
                    '}';
        }

        public static CertificadoBuilder builder() {
            return new CertificadoBuilder();
        }

        public CertificadoBuilder idTipoCertificado(Integer idTipoCertificado) {
            this.idTipoCertificado = idTipoCertificado;
            return this;
        }

        public CertificadoBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public CertificadoBuilder idFuncionario(Integer idFuncionario) {
            this.idFuncionario = idFuncionario;
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
            certificado.dataValidade = this.dataValidade;
            certificado.idTipoCertificado = this.idTipoCertificado;
            certificado.atualizadoPor = this.atualizadoPor;
            certificado.idFuncionario = this.idFuncionario;
            return certificado;
        }
    }
}
