package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.ConjuntoDAO;
import com.joaonardi.gerenciadorocupacional.model.Conjunto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ConjuntoService {
    private static final ObservableList<Conjunto> listaConjuntos = FXCollections.observableArrayList();
    private final ConjuntoDAO conjuntoDAO = new ConjuntoDAO();

    public void carregarConjuntoTipoExameId(int tipoExameId){
        listarConjuntos().clear();
        listaConjuntos.setAll(conjuntoDAO.listarConjuntosPorTipoExameId(tipoExameId));
    }
    public ObservableList<Conjunto> listarConjuntos(){
        return listaConjuntos;
    }

    public Conjunto cadastrarConjunto(Conjunto conjunto){
        Conjunto conjunto1 = conjuntoDAO.cadastrarConjunto(conjunto);
        carregarConjuntoTipoExameId(conjunto.getTipoExame().getId());
        return conjunto1;
    }

    public void deletarConjunto(Conjunto conjunto) {
        conjuntoDAO.deletarConjunto(conjunto.getId());
    }
}
