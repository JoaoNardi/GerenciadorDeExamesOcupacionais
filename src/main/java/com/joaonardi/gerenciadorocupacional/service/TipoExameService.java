package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.TipoExameDAO;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import javafx.collections.ObservableList;

public class TipoExameService {
    private final TipoExameDAO dao = new TipoExameDAO();

    public TipoExame cadastrarTipoExame(TipoExame tipoExame) {
        if (tipoExame.getId() == null) {
            dao.cadastrarTipoExame(tipoExame);
        } else {
            dao.alterarTipoExame(tipoExame.getId(), tipoExame);
        }
        return tipoExame;
    }

    public void deletarTipoExame(TipoExame tipoExame) {
        dao.deletarTipoExame(tipoExame.getId());
    }

    public ObservableList<TipoExame> listarTiposExame() {
        return dao.listarTiposExame();
    }

    public ObservableList<TipoExame> listarTiposExame(boolean ativos) {
        return dao.listarTiposExame();
    }
    public ObservableList<TipoExame> listarTiposExame(boolean ativos, Funcionario funcionario) {
        return dao.listarTiposExame();
    }
    public ObservableList<TipoExame> listarTiposExame(Funcionario funcionario) {
        return dao.listarTiposExame(funcionario.getId());
    }


    public TipoExame getTipoExameMapeadoPorId(int tipoId) {
        return dao.listarTiposExame()
                .stream()
                .filter(t -> t.getId() == tipoId)
                .findFirst()
                .orElse(null);
    }

}
