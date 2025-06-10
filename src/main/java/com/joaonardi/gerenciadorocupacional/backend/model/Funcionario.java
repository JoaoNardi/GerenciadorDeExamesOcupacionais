package com.joaonardi.gerenciadorocupacional.backend.model;

import javax.annotation.processing.Generated;
import java.time.LocalDate;

public class Funcionario {
    private Integer id;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private LocalDate dataAdmissao;
    private Integer setor;

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public Integer getSetor() {
        return setor;
    }

    //builder
    public static final class FuncionarioBuilder {
        private Integer id;
        private String nome;
        private String cpf;
        private LocalDate dataNascimento;
        private LocalDate dataAdmissao;
        private Integer setor;

        //getters
        public Integer getId() {
            return id;
        }

        public String getNome() {
            return nome;
        }

        public String getCpf() {
            return cpf;
        }

        public LocalDate getDataNascimento() {
            return dataNascimento;
        }

        public LocalDate getDataAdmissao() {
            return dataAdmissao;
        }

        public Integer getSetor() {
            return setor;
        }

        private FuncionarioBuilder() {
        }

        public static FuncionarioBuilder builder() {
            return new FuncionarioBuilder();
        }

        public FuncionarioBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public FuncionarioBuilder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public FuncionarioBuilder cpf(String cpf) {
            this.cpf = cpf;
            return this;
        }

        public FuncionarioBuilder dataNascimento(LocalDate dataNascimento) {
            this.dataNascimento = dataNascimento;
            return this;
        }

        public FuncionarioBuilder dataAdmissao(LocalDate dataAdmissao) {
            this.dataAdmissao = dataAdmissao;
            return this;
        }

        public FuncionarioBuilder setor(Integer setor) {
            this.setor = setor;
            return this;
        }

        public Funcionario build() {
            Funcionario funcionario = new Funcionario();
            funcionario.dataNascimento = this.dataNascimento;
            funcionario.dataAdmissao = this.dataAdmissao;
            funcionario.setor = this.setor;
            funcionario.nome = this.nome;
            funcionario.id = this.id;
            funcionario.cpf = this.cpf;
            return funcionario;
        }


    }
    @Override
    public String toString() {
        return "FuncionarioBuilder{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", dataAdmissao=" + dataAdmissao +
                ", setor=" + setor +
                '}';
    }
}
