package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.RelatorioDAO;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.RelatorioItem;
import com.joaonardi.gerenciadorocupacional.model.Tipo;
import com.joaonardi.gerenciadorocupacional.model.TipoDe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class RelatorioService {
    private ObservableList<Tipo> relatorioLista = FXCollections.observableArrayList();
    private RelatorioDAO relatorioDAO = new RelatorioDAO();


    public ObservableList<RelatorioItem> montarRelatorio(Funcionario funcionario, String inputData, LocalDate dataInicial, LocalDate dataFinal, TipoDe tipoDe, boolean exame,
                                                         boolean certificado) {
    return relatorioDAO.montarRelatorio(funcionario,inputData, dataInicial,dataFinal,tipoDe,exame,certificado);
    }
}
