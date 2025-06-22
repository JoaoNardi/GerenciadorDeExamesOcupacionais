package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.SetorDAO;
import com.joaonardi.gerenciadorocupacional.model.Setor;

import java.util.Arrays;
import java.util.List;

public class SetorService {

    private final SetorDAO dao = new SetorDAO();
    public void cadastrarSetor(Setor setor) {
        try {
            dao.cadastrarSetor(setor);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Setor> carregarSetores(){

        return dao.listarSetores();
    }

    public Setor consultarSetorPorId(Integer id) throws Exception {
        return dao.consultarSetor(id);
    }
}
