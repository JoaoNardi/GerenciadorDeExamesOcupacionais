package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.FuncionarioDAO;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class FuncionarioService {

    private final FuncionarioDAO dao = new FuncionarioDAO();

    public void validarCpf(String cpf) throws Exception {
        if((cpf.length() != 11 || cpf == null)) throw new Exception("cpf Invalido");
    }

    public Integer calcularIdade(LocalDate dataNascimento){
        Integer idade = null;
        idade = LocalDate.now().compareTo(dataNascimento);
        return idade;
    }

    public void cadastrarFuncionario(Funcionario funcionario) throws Exception {
        validarCpf(funcionario.getCpf());
        if (funcionario.getId() == null) {
            dao.cadastrarFuncionario(funcionario);
        } else {dao.alterarFuncionario(funcionario.getId(),funcionario);}
    }

    public ObservableList<Funcionario> carregarFuncionarios(boolean inAtivo) throws Exception {
        return dao.listaFuncionariosPorStatus(inAtivo);
    }
}
