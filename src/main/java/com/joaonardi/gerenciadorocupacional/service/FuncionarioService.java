package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.cache.FuncionarioCache;
import com.joaonardi.gerenciadorocupacional.dao.FuncionarioDAO;
import com.joaonardi.gerenciadorocupacional.model.*;
import javafx.collections.ObservableList;

import java.time.LocalDate;


public class FuncionarioService {

    private final FuncionarioDAO dao = new FuncionarioDAO();

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
        FuncionarioCache.carregarFuncionarios(true);
    }

    public Funcionario getFuncionarioMapeadoPorId(int id) {
        return FuncionarioCache.getFuncionarioMapeado(id);
    }

    public ObservableList<Funcionario> listarFuncionarios(boolean inAtivo) throws Exception {
        return dao.listaFuncionariosPorStatus(inAtivo);
    }
}
