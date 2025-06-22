package com.joaonardi.gerenciadorocupacional.model;
import lombok.Getter;

@Getter
public class Setor {
    private Integer id;
    private String area;
    private Integer grauRisco;

    public Setor(){}

    //builder
    public static final class SetorBuilder {
        private Integer id;
        private String area;
        private Integer grauRisco;

        private SetorBuilder() {
        }

        public static SetorBuilder builder() {
            return new SetorBuilder();
        }

        public SetorBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public SetorBuilder area(String area) {
            this.area = area;
            return this;
        }

        public SetorBuilder grauRisco(Integer grauRisco) {
            this.grauRisco = grauRisco;
            return this;
        }

        public Setor build() {
            Setor setor = new Setor();
            setor.area = this.area;
            setor.id = this.id;
            setor.grauRisco = this.grauRisco;
            return setor;
        }

    }
    @Override
    public String toString() {
        return area;

    }
}
