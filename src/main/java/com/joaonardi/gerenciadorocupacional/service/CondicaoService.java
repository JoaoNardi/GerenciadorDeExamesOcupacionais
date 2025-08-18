package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.CondicaoDAO;
import com.joaonardi.gerenciadorocupacional.model.Condicao;
import javafx.collections.ObservableList;

import java.sql.SQLException;

public class CondicaoService {
    private CondicaoDAO condicaoDAO = new CondicaoDAO();

    public void cadastrarCondicao(Condicao condicao){
        condicaoDAO.cadastrarCondicao(condicao);
    }

    public ObservableList<Condicao> listarCondicoesPorTipoExameId(int id){
        return condicaoDAO.listarCondicoesPorTipoExameId(id);
    }

    public void cadastrarListaCondicao(ObservableList<Condicao> listaCondicao){
        condicaoDAO.cadastrarListaCondicao(listaCondicao);
    }

    public ObservableList<Condicao> listarTodasCondicoes(){
        return condicaoDAO.listarTodasCondicoes();

    }
}
