package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.CondicaoDAO;
import com.joaonardi.gerenciadorocupacional.model.Condicao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CondicaoService {
    private CondicaoDAO condicaoDAO = new CondicaoDAO();

    public void cadastrarCondicao(Condicao condicao) {
        condicaoDAO.cadastrarCondicao(condicao);
    }

    public ObservableList<Condicao> listarCondicoesPorTipoExameId(int id) {
        return condicaoDAO.listarCondicoesPorTipoExameId(id);
    }

    public void cadastrarListaCondicao(ObservableList<Condicao> listaCondicao) {
        ObservableList<Condicao> lista = FXCollections.observableArrayList();
        for (Condicao condicao : listaCondicao) {
            if (condicao.getId() == null) {
                lista.add(condicao);
            }
        }
        condicaoDAO.cadastrarListaCondicao(lista);
    }

    public void deletarCondicao(Condicao condicao) {
        condicaoDAO.deletarCondicao(condicao.getId());
    }

    public ObservableList<Condicao> listarTodasCondicoes() {
        return condicaoDAO.listarTodasCondicoes();

    }
}
