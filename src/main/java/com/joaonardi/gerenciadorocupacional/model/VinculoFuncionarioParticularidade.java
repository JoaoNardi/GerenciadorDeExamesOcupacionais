package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@ToString
@Getter
public class VinculoFuncionarioParticularidade {
    private Integer id;
    private Funcionario funcionario;
    private Particularidade particularidade;
    private String motivo;
    private LocalDate dataInclusao;
    private LocalDate dataExclusao;

    public static final class VinculoFuncionarioParticularidadeBuilder {
        private Integer id;
        private Funcionario funcionario;
        private Particularidade particularidade;
        private String motivo;
        private LocalDate dataInclusao;
        private LocalDate dataExclusao;

        private VinculoFuncionarioParticularidadeBuilder() {
        }

        public static VinculoFuncionarioParticularidadeBuilder builder() {
            return new VinculoFuncionarioParticularidadeBuilder();
        }

        public VinculoFuncionarioParticularidadeBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public VinculoFuncionarioParticularidadeBuilder funcionario(Funcionario funcionario) {
            this.funcionario = funcionario;
            return this;
        }

        public VinculoFuncionarioParticularidadeBuilder particularidade(Particularidade particularidade) {
            this.particularidade = particularidade;
            return this;
        }

        public VinculoFuncionarioParticularidadeBuilder motivo(String motivo) {
            this.motivo = motivo;
            return this;
        }

        public VinculoFuncionarioParticularidadeBuilder dataInclusao(LocalDate dataInclusao) {
            this.dataInclusao = dataInclusao;
            return this;
        }

        public VinculoFuncionarioParticularidadeBuilder dataExclusao(LocalDate dataExclusao) {
            this.dataExclusao = dataExclusao;
            return this;
        }

        public VinculoFuncionarioParticularidade build() {
            VinculoFuncionarioParticularidade vinculoFuncionarioParticularidade = new VinculoFuncionarioParticularidade();
            vinculoFuncionarioParticularidade.funcionario = this.funcionario;
            vinculoFuncionarioParticularidade.motivo = this.motivo;
            vinculoFuncionarioParticularidade.dataExclusao = this.dataExclusao;
            vinculoFuncionarioParticularidade.dataInclusao = this.dataInclusao;
            vinculoFuncionarioParticularidade.id = this.id;
            vinculoFuncionarioParticularidade.particularidade = this.particularidade;
            return vinculoFuncionarioParticularidade;
        }
    }
}
