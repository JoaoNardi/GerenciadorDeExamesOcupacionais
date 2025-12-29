package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.SetorDAO;
import com.joaonardi.gerenciadorocupacional.model.Setor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SetorService {

    private final SetorDAO dao = new SetorDAO();

    public void cadastrarSetor(Setor setor) {
        if (setor.getId() == null) {
            dao.cadastrarSetor(setor);
        } else {
            dao.alterarSetor(setor.getId(), setor);
        }
    }

    public ObservableList<Setor> listarSetores() {
        return dao.listarSetores();
    }

    public void deletarSetor(Setor setor){
        dao.deletarSetor(setor.getId());
    }
}
