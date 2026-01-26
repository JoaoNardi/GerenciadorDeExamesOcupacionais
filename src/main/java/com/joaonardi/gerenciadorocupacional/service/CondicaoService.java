package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.CondicaoDAO;
import com.joaonardi.gerenciadorocupacional.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CondicaoService {
    private static final ObservableList<Condicao> listaCondicoes = FXCollections.observableArrayList();
    private final CondicaoDAO condicaoDAO = new CondicaoDAO();

    public void carregarCondicoesPorConjuntoId(int id){
        listaCondicoes.clear();
        listaCondicoes.setAll(condicaoDAO.listarCondicoesPorConjuntoId(id));
    }

    public ObservableList<Condicao> listarCondicoes() {
        return listaCondicoes;
    }

    public void cadastrarCondicao(Condicao condicao){
        try {
            condicaoDAO.cadastrarCondicao(condicao);
            listaCondicoes.add(condicao);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void deletarCondicao(Condicao condicao) {
        condicaoDAO.deletarCondicao(condicao.getId());
    }
}
