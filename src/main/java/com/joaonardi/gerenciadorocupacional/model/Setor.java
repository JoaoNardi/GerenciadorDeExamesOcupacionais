package com.joaonardi.gerenciadorocupacional.model;
import lombok.Getter;

@Getter
public class Setor {
    private Integer id;
    private String area;

    public Setor(){}

    @Override
    public String toString() {
        return area;

    }

    public static final class SetorBuilder {
        private Integer id;
        private String area;

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

        public Setor build() {
            Setor setor = new Setor();
            setor.id = this.id;
            setor.area = this.area;
            return setor;
        }
    }
}
