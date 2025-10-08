package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class RelatorioItem extends Tipo{
    private String origem;
    private int tipoId;


    public static final class RelatorioItemBuilder {
        private String origem;
        private int tipoId;
        private Integer id;
        private Integer idFuncionario;
        private LocalDate dataEmissao;
        private LocalDate dataValidade;
        private Integer atualizadoPor;

        private RelatorioItemBuilder() {
        }

        public static RelatorioItemBuilder builder() {
            return new RelatorioItemBuilder();
        }

        public RelatorioItemBuilder origem(String origem) {
            this.origem = origem;
            return this;
        }

        public RelatorioItemBuilder tipoId(int tipoId) {
            this.tipoId = tipoId;
            return this;
        }

        public RelatorioItemBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public RelatorioItemBuilder idFuncionario(Integer idFuncionario) {
            this.idFuncionario = idFuncionario;
            return this;
        }

        public RelatorioItemBuilder dataEmissao(LocalDate dataEmissao) {
            this.dataEmissao = dataEmissao;
            return this;
        }

        public RelatorioItemBuilder dataValidade(LocalDate dataValidade) {
            this.dataValidade = dataValidade;
            return this;
        }

        public RelatorioItemBuilder atualizadoPor(Integer atualizadoPor) {
            this.atualizadoPor = atualizadoPor;
            return this;
        }

        public RelatorioItem build() {
            RelatorioItem relatorioItem = new RelatorioItem();
            relatorioItem.setId(id);
            relatorioItem.idFuncionario = this.idFuncionario;
            relatorioItem.origem = this.origem;
            relatorioItem.atualizadoPor = this.atualizadoPor;
            relatorioItem.tipoId = this.tipoId;
            relatorioItem.dataEmissao = this.dataEmissao;
            relatorioItem.dataValidade = this.dataValidade;
            return relatorioItem;
        }
    }
}
