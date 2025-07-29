package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.cache.ExameCache;
import com.joaonardi.gerenciadorocupacional.cache.FuncionarioCache;
import com.joaonardi.gerenciadorocupacional.cache.SetorCache;
import com.joaonardi.gerenciadorocupacional.cache.TipoExameCache;

public class MainService {

    public void loadInicial() throws Exception {
        ExameCache.carregarExamesVigentes();
        TipoExameCache.carregarTiposExames();
        FuncionarioCache.carregarFuncionarios(true);
        SetorCache.carregarSetores();
    }
}
