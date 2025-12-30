package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.FuncionarioDAO;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import javafx.collections.ObservableList;

import java.time.LocalDate;


public class FuncionarioService {

    private final FuncionarioDAO dao = new FuncionarioDAO();
    public static ObservableList<Funcionario> funcionariosList;

    public ObservableList<Funcionario> listarFuncionariosPorStatus(boolean status) {
        return dao.listaFuncionariosPorStatus(status);
    }

    public ObservableList<Funcionario> listarTodosFuncionarios() {
        return dao.listaFuncionariosPorStatus();
    }

    public void validarCpf(String cpf) {
        if ((cpf.length() != 11 || cpf == null)) throw new IllegalArgumentException("cpf Invalido");
    }

    public Integer calcularIdade(LocalDate dataNascimento) {
        int idade;
        idade = LocalDate.now().compareTo(dataNascimento);
        return idade;
    }

    public void cadastrarFuncionario(Funcionario funcionario) {
        validarCpf(funcionario.getCpf());
        if (funcionario.getId() == null) {
            dao.cadastrarFuncionario(funcionario);
        } else {
            dao.alterarFuncionario(funcionario.getId(), funcionario);
        }
    }

    public void ativarInativar(Funcionario funcionario) {
        dao.ativarInativarFuncionario(funcionario);
    }

}
