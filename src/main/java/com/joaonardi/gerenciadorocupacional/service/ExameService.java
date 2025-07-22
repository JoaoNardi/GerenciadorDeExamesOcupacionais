package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.ExameDAO;
import com.joaonardi.gerenciadorocupacional.model.Exame;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class ExameService {
    private ExameDAO exameDAO = new ExameDAO();

    public void lancarExame(Exame exame){
        exameDAO.cadastrarExame(exame);
    }

    public LocalDate calcularValidadeExame(LocalDate emissaoExame, TipoExame tipoExame){
        LocalDate dataValidade = emissaoExame.plusDays(tipoExame.getPeriodicidade());
        return dataValidade;
    }

    public ObservableList<Exame> listarExames(){
        return exameDAO.listarExames();
    }

}
