package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.TipoExameDAO;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import javafx.collections.ObservableList;

import java.util.Map;
import java.util.stream.Collectors;

public class TipoExameService {
    private final TipoExameDAO dao = new TipoExameDAO();
    private static Map<Integer, TipoExame> tipoExameMap;

    public TipoExame cadastrarTipoExame(TipoExame tipoExame) {
        if (tipoExame.getId() == null) {
            dao.cadastrarTipoExame(tipoExame);
        } else {
            dao.alterarTipoExame(tipoExame.getId(), tipoExame);
        }
        carregarTipoExames();
        return tipoExame;

    }

    public void deletarTipoExame(int id) {
        dao.deletarTipoExame(id);
    }

    public void carregarTipoExames() {
        tipoExameMap = listarTiposExame().stream()
                .collect(Collectors.toMap(TipoExame::getId, f -> f));

    }
    public ObservableList<TipoExame> listarTiposExame() {
        return dao.listarTiposExame();
    }

    public TipoExame getTipoExameMapeadoPorId(Integer id) {
        return tipoExameMap.get(id);
    }

}
