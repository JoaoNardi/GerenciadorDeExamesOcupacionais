package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.cache.TipoExameCache;
import com.joaonardi.gerenciadorocupacional.dao.ExameDAO;
import com.joaonardi.gerenciadorocupacional.dao.TipoExameDAO;
import com.joaonardi.gerenciadorocupacional.model.Exame;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import javafx.collections.ObservableList;

public class TipoExameService {
    private final TipoExameDAO dao = new TipoExameDAO();

    public TipoExame cadastrarTipoExame(TipoExame tipoExame){
        if (tipoExame.getId() == null) {
            dao.cadastrarTipoExame(tipoExame);
        } else {
            dao.alterarTipoExame(tipoExame.getId(), tipoExame);
        }
        return tipoExame;
    }

    public void deletarTipoExame(int id){
        dao.deletarTipoExame(id);
    }

    public TipoExame getTipoExameMapeadoPorId(int id){
        return TipoExameCache.getTipoExameMapeado(id);
    }

    public ObservableList<TipoExame> carregarTiposExame() {
        return dao.listarTiposExame();
    }
}
