package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class VinculoFuncionarioParticularidade {
    private int id;
    private Funcionario funcionario;
    private Particularidade particularidade;
    private String motivo;

    public static final class VinculoFuncionarioParticularidadeBuilder {
        private int id;
        private Funcionario funcionario;
        private Particularidade particularidade;
        private String motivo;

        private VinculoFuncionarioParticularidadeBuilder() {
        }

        public static VinculoFuncionarioParticularidadeBuilder builder() {
            return new VinculoFuncionarioParticularidadeBuilder();
        }

        public VinculoFuncionarioParticularidadeBuilder id(int id) {
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

        public VinculoFuncionarioParticularidade build() {
            VinculoFuncionarioParticularidade vinculoFuncionarioParticularidade = new VinculoFuncionarioParticularidade();
            vinculoFuncionarioParticularidade.id = this.id;
            vinculoFuncionarioParticularidade.particularidade = this.particularidade;
            vinculoFuncionarioParticularidade.motivo = this.motivo;
            vinculoFuncionarioParticularidade.funcionario = this.funcionario;
            return vinculoFuncionarioParticularidade;
        }
    }
}
