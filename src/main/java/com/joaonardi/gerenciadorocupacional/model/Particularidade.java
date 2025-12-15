package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class Particularidade {
    @Setter
    private Integer id;
    private String nome;
    private String descricao;
    private TipoExame tipoExame;
    private Integer periodicidade;

    public static final class ParticularidadeBuilder {
        private Integer id;
        private String nome;
        private String descricao;
        private TipoExame tipoExame;
        private Integer periodicidade;

        private ParticularidadeBuilder() {
        }

        public static ParticularidadeBuilder builder() {
            return new ParticularidadeBuilder();
        }

        public ParticularidadeBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public ParticularidadeBuilder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public ParticularidadeBuilder descricao(String descricao) {
            this.descricao = descricao;
            return this;
        }

        public ParticularidadeBuilder tipoExame(TipoExame tipoExame) {
            this.tipoExame = tipoExame;
            return this;
        }

        public ParticularidadeBuilder periodicidade(Integer periodicidade) {
            this.periodicidade = periodicidade;
            return this;
        }

        public Particularidade build() {
            Particularidade particularidade = new Particularidade();
            particularidade.setId(id);
            particularidade.nome = this.nome;
            particularidade.descricao = this.descricao;
            particularidade.tipoExame = this.tipoExame;
            particularidade.periodicidade = this.periodicidade;
            return particularidade;
        }

    }
}
