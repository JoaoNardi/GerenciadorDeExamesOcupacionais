package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.TipoCertificadoDAO;
import com.joaonardi.gerenciadorocupacional.model.TipoCertificado;
import javafx.collections.ObservableList;

public class TipoCertificadoService {
    private final TipoCertificadoDAO dao = new TipoCertificadoDAO();

    public void cadastrarTipoCertificado(TipoCertificado tipoCertificado){
        if (tipoCertificado.getId() == null) {
            dao.cadastrarTipoCertificado(tipoCertificado);
        } else {
            dao.alterarTipoCertificado(tipoCertificado.getId(), tipoCertificado);
        }
    }

    public ObservableList<TipoCertificado> carregarTiposCertificado() {
        return dao.listarTiposCertificado();
    }
}
