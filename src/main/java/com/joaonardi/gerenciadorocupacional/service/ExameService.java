package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.cache.ExameCache;
import com.joaonardi.gerenciadorocupacional.dao.ExameDAO;
import com.joaonardi.gerenciadorocupacional.model.Exame;
import com.joaonardi.gerenciadorocupacional.model.Periodicidade;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class ExameService {
    private ExameDAO exameDAO = new ExameDAO();

    public void lancarExame(Exame exame) {
        exameDAO.cadastrarExame(exame);
        ExameCache.carregarExamesVigentes();
    }

    public LocalDate calcularValidadeExame(LocalDate emissaoExame, TipoExame tipoExame) {
        LocalDate dataValidade = emissaoExame.plusMonths(tipoExame.getPeriodicidade());
        return dataValidade;
    }

    public ObservableList<Exame> listarExames() {
        ExameCache.carregarExamesVigentes();
        return ExameCache.todosExames;
    }

    public String vencimentos(TableColumn.CellDataFeatures<Exame, String> exame) {
        Integer dias = (int) ChronoUnit.DAYS.between(LocalDate.now(), exame.getValue().getDataValidade());
        String status = "Faltam: " + dias + " para o vencimento";
        if (dias < 0) {
            status = "Vencido " + dias + " de atraso";
        } else if (dias == 0) {
            status = "Vence Hoje";
        } else if (dias <= 7) {
            status = "Vence esta semana, em : " + dias + " dias ";
        } else if (dias <= 30) {
            status = "Vence dentro de um mês, em : " + dias + " dias ";
        } else if (dias <= 182) {
            status = "Vence neste semestre, em : " + dias + " dias ";
        }

        return status;
    }

    public ObservableList<Exame> listarExamePorVencimento(int diasVencimento) {

        List<Exame> list = ExameCache.todosExames;
        LocalDate hoje = LocalDate.now();
        if (diasVencimento == 183) {
            return FXCollections.observableArrayList(list);
        } else {
            List<Exame> list2 = list.stream()
                    .filter(f -> {
                        LocalDate validade = f.getDataValidade();
                        if (validade == null) {
                            return false;
                        }
                        int dias = (int) ChronoUnit.DAYS.between(hoje, validade);

                        if (diasVencimento == 0) {
                            return dias <= 0;
                        } else if (diasVencimento == 7) {
                            return dias > 0 && dias <= 7;
                        } else if (diasVencimento == 30) {
                            return dias > 7 && dias <= 30;
                        } else if (diasVencimento == 182) {
                            return dias > 30 && dias <= 182;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());

            return FXCollections.observableArrayList(list2);
        }
    }

}
