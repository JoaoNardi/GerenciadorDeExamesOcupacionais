package com.joaonardi.gerenciadorocupacional.cache;

import com.joaonardi.gerenciadorocupacional.dao.TipoExameDAO;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;

import java.util.Map;
import java.util.stream.Collectors;

public class TipoExameCache {
    private static Map<Integer, TipoExame> tipoExameMap;
    private static TipoExameDAO tipoExameDAO = new TipoExameDAO();
    public static void carregarTiposExames() {
        tipoExameMap = tipoExameDAO.listarTiposExame().stream()
                .collect(Collectors.toMap(TipoExame::getId,f -> f));

    }
    public static TipoExame getTipoExameMapeado(int id){
        return tipoExameMap.get(id);
    }
}
