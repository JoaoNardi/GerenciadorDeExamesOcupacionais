package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.SetorDAO;
import com.joaonardi.gerenciadorocupacional.model.Setor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Map;
import java.util.stream.Collectors;


public class SetorService {

    private final SetorDAO dao = new SetorDAO();
    private static Map<Integer, String> setoresMap;
    private static SetorDAO setorDAO = new SetorDAO();
    ObservableList<Setor> setorList = FXCollections.observableArrayList();

    public void cadastrarSetor(Setor setor) {
        if (setor.getId() == null) {
            dao.cadastrarSetor(setor);
        } else {
            dao.alterarSetor(setor.getId(), setor);
        }

    }

    public void carregarSetores(){
        setorList = dao.listarSetores();
        setoresMap = setorList.stream()
                .collect(Collectors.toMap(Setor::getId, Setor::getArea));
    }

    public ObservableList<Setor> listarSetores() {
        return setorList;
    }

    public String getSetorMapeado(int id){
        return setoresMap.getOrDefault(id,"Sem Setor") ;
    }

    public Setor consultarSetorPorId(Integer id) throws Exception {
        return dao.consultarSetor(id);
    }
}
