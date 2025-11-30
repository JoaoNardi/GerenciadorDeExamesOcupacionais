package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.ConjuntoDAO;
import com.joaonardi.gerenciadorocupacional.model.Conjunto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ConjuntoService {
    private static ObservableList<Conjunto> listaConjuntos = FXCollections.observableArrayList();
    private final ConjuntoDAO conjuntoDAO = new ConjuntoDAO();

    public void carregarConjunto(){
//        listaConjuntos.setAll(conjuntoDAO.listarConjuntosPorTipoExameId())
    }
    public void carregarConjuntoTipoExameId(int tipoExameId){
        listaConjuntos.setAll(conjuntoDAO.listarConjuntosPorTipoExameId(tipoExameId));
    }
    public ObservableList<Conjunto> listarConjuntos(){
        return listaConjuntos;
    }

    public Conjunto cadastrarConjunto(Conjunto conjunto){
        Conjunto conjunto1 = conjuntoDAO.cadastrarConjunto(conjunto);
        carregarConjuntoTipoExameId(conjunto.getTipoExameId());
        return conjunto1;

    }

    public void deletarConjunto(Conjunto conjunto) {
        conjuntoDAO.deletarConjunto(conjunto.getId());
    }
}
