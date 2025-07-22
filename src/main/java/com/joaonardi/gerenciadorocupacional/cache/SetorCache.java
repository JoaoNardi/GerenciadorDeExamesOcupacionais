package com.joaonardi.gerenciadorocupacional.cache;

import com.joaonardi.gerenciadorocupacional.dao.SetorDAO;
import com.joaonardi.gerenciadorocupacional.model.Setor;

import java.util.Map;
import java.util.stream.Collectors;

public class SetorCache {

    private static Map<Integer, String> setoresMap;
    private static SetorDAO setorDAO = new SetorDAO();

    public static void carregarSetores(){
        setoresMap = setorDAO.listarSetores().stream()
                .collect(Collectors.toMap(Setor::getId, Setor::getArea));
    }

    public static String getSetorMapeado(int id){

        return setoresMap.getOrDefault(id,"Sem Setor") ;
    }



}
