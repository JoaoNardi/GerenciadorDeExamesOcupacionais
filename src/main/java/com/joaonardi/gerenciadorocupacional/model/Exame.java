package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
@Getter
public class Exame extends Tipo {
    private Integer idTipoExame;

    public static final class ExameBuilder {
        private Integer idTipoExame;
        private Integer id;
        private Integer idFuncionario;
        private LocalDate dataEmissao;
        private LocalDate dataValidade;
        private Integer atualizadoPor;

        @Override
        public String toString() {
            return "ExameBuilder{" +
                    "idTipoExame=" + idTipoExame +
                    ", id=" + id +
                    ", idFuncionario=" + idFuncionario +
                    ", dataEmissao=" + dataEmissao +
                    ", dataValidade=" + dataValidade +
                    ", atualizadoPor=" + atualizadoPor +
                    '}';
        }

        private ExameBuilder() {
        }

        public static ExameBuilder builder() {
            return new ExameBuilder();
        }

        public ExameBuilder idTipoExame(Integer idTipoExame) {
            this.idTipoExame = idTipoExame;
            return this;
        }

        public ExameBuilder id(Integer id) {
            this.id = id;
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
            exame.setId(id);
            exame.dataEmissao = this.dataEmissao;
            exame.idTipoExame = this.idTipoExame;
            exame.dataValidade = this.dataValidade;
            exame.atualizadoPor = this.atualizadoPor;
            exame.idFuncionario = this.idFuncionario;
            return exame;
        }
    }
}
