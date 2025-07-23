package com.joaonardi.gerenciadorocupacional.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class Exame {
    private Integer id;
    private Integer idTipoExame;
    private Integer idFuncionario;
    private LocalDate dataEmissao;
    private LocalDate dataValidade;
    private Integer atualizadoPor;


    public static final class ExameBuilder {
        private Integer id;
        private Integer idTipoExame;
        private Integer idFuncionario;
        private LocalDate dataEmissao;
        private LocalDate dataValidade;
        private Integer atualizadoPor;

        private ExameBuilder() {
        }

        public static ExameBuilder builder() {
            return new ExameBuilder();
        }

        public ExameBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public ExameBuilder idTipoExame(Integer idTipoExame) {
            this.idTipoExame = idTipoExame;
            return this;
        }

        public ExameBuilder idFuncionario(Integer idFuncionario) {
            this.idFuncionario = idFuncionario;
            return this;
        }

        public ExameBuilder dataEmissao(LocalDate dataEmissao) {
            this.dataEmissao = dataEmissao;
            return this;
        }

        public ExameBuilder dataValidade(LocalDate dataValidade) {
            this.dataValidade = dataValidade;
            return this;
        }

        public ExameBuilder atualizadoPor(Integer atualizadoPor) {
            this.atualizadoPor = atualizadoPor;
            return this;
        }

        public Exame build() {
            Exame exame = new Exame();
            exame.id = this.id;
            exame.atualizadoPor = this.atualizadoPor;
            exame.idFuncionario = this.idFuncionario;
            exame.dataEmissao = this.dataEmissao;
            exame.dataValidade = this.dataValidade;
            exame.idTipoExame = this.idTipoExame;
            return exame;
        }
    }
}
