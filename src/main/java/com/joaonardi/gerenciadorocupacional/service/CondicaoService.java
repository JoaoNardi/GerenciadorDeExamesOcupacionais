package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.CondicaoDAO;
import com.joaonardi.gerenciadorocupacional.model.Condicao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CondicaoService {
    private static ObservableList<Condicao> listaCondicoes = FXCollections.observableArrayList();
    private final CondicaoDAO condicaoDAO = new CondicaoDAO();

//    public void carregarCondicoesPorTipoExameId(int id) {
//        listaCondicoes.clear();
//        listaCondicoes.setAll(condicaoDAO.listarCondicoesPorTipoExameId(id));
//    }

    public void carregarCondicoesPorConjuntoId(int id){
        listaCondicoes.clear();
        listaCondicoes.setAll(condicaoDAO.listarCondicoesPorConjuntoId(id));
    }
    public ObservableList<Condicao> listarCondicoes() {
        return listaCondicoes;
    }

    public void cadastrarListaCondicao(ObservableList<Condicao> listaCondicao) {
        ObservableList<Condicao> lista = FXCollections.observableArrayList();
        for (Condicao condicao : listaCondicao) {
            if (condicao.getId() == null) {
                if (condicao.getParametro() == null){
                    continue;
                }
                lista.add(condicao);
            }
        }
        condicaoDAO.cadastrarListaCondicao(lista);
    }

    public void deletarCondicao(Condicao condicao) {
        condicaoDAO.deletarCondicao(condicao.getId());
    }

}
