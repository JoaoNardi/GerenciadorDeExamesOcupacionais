package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.SetorDAO;
import com.joaonardi.gerenciadorocupacional.model.Setor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SetorService {

    private final SetorDAO dao = new SetorDAO();
    ObservableList<Setor> setorList = FXCollections.observableArrayList();

    public void cadastrarSetor(Setor setor) {
        if (setor.getId() == null) {
            dao.cadastrarSetor(setor);
        } else {
            dao.alterarSetor(setor.getId(), setor);
        }
        carregarSetores();
    }

    public void carregarSetores(){
        setorList = dao.listarSetores();
    }

    public ObservableList<Setor> listarSetores() {
        return setorList;
    }
}
