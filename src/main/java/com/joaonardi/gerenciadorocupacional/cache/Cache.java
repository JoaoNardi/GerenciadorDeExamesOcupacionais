package com.joaonardi.gerenciadorocupacional.cache;

public class Cache {

    public static void recarregarTudo(){
        CertificadoCache.carregarCertificados();
        ExameCache.carregarExames();
        FuncionarioCache.carregarFuncionarios();
        SetorCache.carregarSetores();
        TipoCertificadoCache.carregarTiposCertificados();
        TipoExameCache.carregarTiposExames();
        }
}
