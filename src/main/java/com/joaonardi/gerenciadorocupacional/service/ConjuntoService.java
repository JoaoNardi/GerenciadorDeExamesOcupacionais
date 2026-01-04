package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.ConjuntoDAO;
import com.joaonardi.gerenciadorocupacional.model.Conjunto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ConjuntoService {
    private static final ObservableList<Conjunto> listaConjuntos = FXCollections.observableArrayList();
    private final ConjuntoDAO conjuntoDAO = new ConjuntoDAO();


    public ObservableList<Conjunto> listarConjuntos(int tipoExameId){
        return conjuntoDAO.listarConjuntosPorTipoExameId(tipoExameId);
    }

    public Conjunto cadastrarConjunto(Conjunto conjunto){
        Conjunto conjunto1 = conjuntoDAO.cadastrarConjunto(conjunto);
        return conjunto1;
    }

    public void deletarConjunto(Conjunto conjunto) {
        conjuntoDAO.deletarConjunto(conjunto.getId());
    }
}
