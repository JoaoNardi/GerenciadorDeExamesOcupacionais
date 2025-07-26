package com.joaonardi.gerenciadorocupacional.cache;

import com.joaonardi.gerenciadorocupacional.dao.FuncionarioDAO;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import javafx.collections.ObservableList;

import java.util.Map;
import java.util.stream.Collectors;

public class FuncionarioCache {
    private static Map<Integer, Funcionario> funcionariosMap;
    private static FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    public static ObservableList<Funcionario> todosFuncionarios;

    public static void carregarFuncionarios(boolean inAtivo) throws Exception {
        todosFuncionarios = funcionarioDAO.listaFuncionariosPorStatus(inAtivo);
        funcionariosMap = funcionarioDAO.listaFuncionariosPorStatus(inAtivo).stream()
                .collect(Collectors.toMap(Funcionario::getId, f->f));
    }
    public static Funcionario getFuncionarioMapeado(int id){
        return funcionariosMap.get(id);
    }
}
