package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.cache.SetorCache;
import com.joaonardi.gerenciadorocupacional.dao.SetorDAO;
import com.joaonardi.gerenciadorocupacional.model.Setor;
import javafx.collections.ObservableList;


public class SetorService {

    private final SetorDAO dao = new SetorDAO();
    public void cadastrarSetor(Setor setor) {
        if (setor.getId() == null){
            dao.cadastrarSetor(setor);
        } else {
            dao.alterarSetor(setor.getId(),setor);
        }

    }

    public ObservableList<Setor> carregarSetores(){
        return dao.listarSetores();
    }

    public String getSetorMapeadoPorId(int id){
        return SetorCache.getSetorMapeado(id);
    }

    public Setor consultarSetorPorId(Integer id) throws Exception {
        return dao.consultarSetor(id);
    }
}
