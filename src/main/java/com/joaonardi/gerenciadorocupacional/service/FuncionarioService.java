package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.FuncionarioDAO;
import com.joaonardi.gerenciadorocupacional.model.*;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;


public class FuncionarioService {

    private final FuncionarioDAO dao = new FuncionarioDAO();
    private static Map<Integer, Funcionario> funcionariosMap;
    public static ObservableList<Funcionario> funcionariosList;

    public void carregarFuncionariosPorStatus(boolean ativos){
     funcionariosList = dao.listaFuncionariosPorStatus(ativos);
     funcionariosMap = funcionariosList.stream()
             .collect(Collectors.toMap(Funcionario::getId, f->f));
    }

    public void carregarTodosFuncionarios(){
        funcionariosList = dao.listaFuncionariosPorStatus();
        funcionariosMap = funcionariosList.stream()
                .collect(Collectors.toMap(Funcionario::getId, f->f));
    }

    public void validarCpf(String cpf) throws Exception {
        if ((cpf.length() != 11 || cpf == null)) throw new Exception("cpf Invalido");
    }

    public Integer calcularIdade(LocalDate dataNascimento) {
        Integer idade = null;
        idade = LocalDate.now().compareTo(dataNascimento);
        return idade;
    }

    public void cadastrarFuncionario(Funcionario funcionario) throws Exception {
        validarCpf(funcionario.getCpf());
        if (funcionario.getId() == null) {
            dao.cadastrarFuncionario(funcionario);
        } else {
            dao.alterarFuncionario(funcionario.getId(), funcionario);
        }
        carregarFuncionariosPorStatus(true);
    }

    public Funcionario getFuncionarioMapeadoPorId(int id) {
        return funcionariosMap.get(id);
    }

    public ObservableList<Funcionario> listarFuncionarios() {
        return funcionariosList;
    }
}
