package com.joaonardi.gerenciadorocupacional.model;

import lombok.Getter;
import java.time.LocalDate;


@Getter
public class Funcionario {
    private Integer id;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private LocalDate dataAdmissao;
    private Integer setor;
    private Boolean ativo;


    public Funcionario() {
    }

    //builder
    public static final class FuncionarioBuilder {
        private Integer id;
        private String nome;
        private String cpf;
        private LocalDate dataNascimento;
        private LocalDate dataAdmissao;
        private Integer setor;
        private Boolean ativo;

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

        public FuncionarioBuilder ativo(Boolean ativo) {
            this.ativo = ativo;
            return this;
        }

        public Funcionario build() {
            Funcionario funcionario = new Funcionario();
            funcionario.dataAdmissao = this.dataAdmissao;
            funcionario.nome = this.nome;
            funcionario.setor = this.setor;
            funcionario.cpf = this.cpf;
            funcionario.dataNascimento = this.dataNascimento;
            funcionario.id = this.id;
            funcionario.ativo = this.ativo;
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
