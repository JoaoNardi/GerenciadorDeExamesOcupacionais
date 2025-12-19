package com.joaonardi.gerenciadorocupacional.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@EqualsAndHashCode
@ToString
@Getter
public class Funcionario {
    private Integer id;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private LocalDate dataAdmissao;
    private Setor setor;
    private Boolean ativo;


    public Funcionario() {
    }

    public static final class FuncionarioBuilder {
        private Integer id;
        private String nome;
        private String cpf;
        private LocalDate dataNascimento;
        private LocalDate dataAdmissao;
        private Setor setor;
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

        public FuncionarioBuilder setor(Setor setor) {
            this.setor = setor;
            return this;
        }

        public FuncionarioBuilder ativo(Boolean ativo) {
            this.ativo = ativo;
            return this;
        }

        public Funcionario build() {
            Funcionario funcionario = new Funcionario();
            funcionario.id = this.id;
            funcionario.cpf = this.cpf;
            funcionario.nome = this.nome;
            funcionario.dataAdmissao = this.dataAdmissao;
            funcionario.setor = this.setor;
            funcionario.ativo = this.ativo;
            funcionario.dataNascimento = this.dataNascimento;
            return funcionario;
        }
    }
}
