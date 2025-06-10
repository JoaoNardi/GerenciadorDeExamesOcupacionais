package com.joaonardi.gerenciadorocupacional.backend.model;

public class Certificado {
    private Integer id;
    private String tipo;

    //geters
    public Integer getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    //builders
    public static final class CertificadoBuilder {
        private Integer id;
        private String tipo;

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

        public Certificado build() {
            Certificado certificado = new Certificado();
            certificado.tipo = this.tipo;
            certificado.id = this.id;
            return certificado;
        }
    }
}
