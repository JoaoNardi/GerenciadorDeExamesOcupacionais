package com.joaonardi.gerenciadorocupacional.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.LocalDate;

@Entity
public class Certificado {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Getter
    private String tipo;
    @Getter
    private Integer dataValidade;

    public Certificado(){}

    public static final class CertificadoBuilder {
        private Integer id;
        private String tipo;
        private Integer dataValidade;

        private CertificadoBuilder() {
        }

        public static CertificadoBuilder builder() {
            return new CertificadoBuilder();
        }

        public CertificadoBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public CertificadoBuilder tipo(String tipo) {
            this.tipo = tipo;
            return this;
        }

        public CertificadoBuilder dataValidade(Integer dataValidade) {
            this.dataValidade = dataValidade;
            return this;
        }

        public Certificado build() {
            Certificado certificado = new Certificado();
            certificado.id = this.id;
            certificado.tipo = this.tipo;
            certificado.dataValidade = this.dataValidade;
            return certificado;
        }
    }
}
