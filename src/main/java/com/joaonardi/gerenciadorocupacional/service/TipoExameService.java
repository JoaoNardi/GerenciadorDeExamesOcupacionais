package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.ExameDAO;
import com.joaonardi.gerenciadorocupacional.dao.TipoExameDAO;
import com.joaonardi.gerenciadorocupacional.model.Exame;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;

public class TipoExameService {
    private final TipoExameDAO dao = new TipoExameDAO();

    public void cadastrarTipoExame(TipoExame tipoExame){
        try {
            dao.cadastrarTipoExame(tipoExame);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
