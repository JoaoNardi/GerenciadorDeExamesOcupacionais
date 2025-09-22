package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.TipoCertificadoDAO;
import com.joaonardi.gerenciadorocupacional.model.TipoCertificado;
import javafx.collections.ObservableList;

import java.util.Map;
import java.util.stream.Collectors;

public class TipoCertificadoService {
    private final TipoCertificadoDAO dao = new TipoCertificadoDAO();
    private static Map<Integer, TipoCertificado> tipoCertificadoMap;

    public void cadastrarTipoCertificado(TipoCertificado tipoCertificado){
        if (tipoCertificado.getId() == null) {
            dao.cadastrarTipoCertificado(tipoCertificado);
        } else {
            dao.alterarTipoCertificado(tipoCertificado.getId(), tipoCertificado);
        }
        carregarTiposCertificado();
    }

    public void carregarTiposCertificado() {
        tipoCertificadoMap = listarTiposCertificados().stream()
                .collect(Collectors.toMap(TipoCertificado::getId, f -> f));

    }
    public ObservableList<TipoCertificado> listarTiposCertificados(){
        return dao.listarTiposCertificado();
    }

    public TipoCertificado getTipoCertificadoMapeadoPorId(Integer id) {
        return tipoCertificadoMap.get(id);
    }
}
