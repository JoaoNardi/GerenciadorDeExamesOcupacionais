package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.FuncionarioDAO;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;

import java.util.ArrayList;
import java.util.List;


public class FuncionarioService {

    private final FuncionarioDAO dao = new FuncionarioDAO();

    private void validarCpf(String cpf) throws Exception {
        if((cpf.length() != 11 || cpf == null)) throw new Exception("cpf Invalido");
    }

    public void cadastrarFuncionario(Funcionario funcionario) throws Exception {
        validarCpf(funcionario.getCpf());
        if (funcionario.getId() == null) {
            dao.cadastrarFuncionario(funcionario);
        } else {dao.alterarFuncionario(funcionario.getId(),funcionario);}
    }

    public ArrayList<Funcionario> carregarFuncionarios() throws Exception {
        return dao.listaFuncionariosPorStatus(true);
    }
}
