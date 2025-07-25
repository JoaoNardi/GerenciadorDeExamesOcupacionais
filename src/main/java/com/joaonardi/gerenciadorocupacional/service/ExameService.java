package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.cache.ExameCache;
import com.joaonardi.gerenciadorocupacional.dao.ExameDAO;
import com.joaonardi.gerenciadorocupacional.model.Exame;
import com.joaonardi.gerenciadorocupacional.model.Periodicidade;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ExameService {
    private ExameDAO exameDAO = new ExameDAO();

    public void lancarExame(Exame exame) {
        exameDAO.cadastrarExame(exame);
    }

    public LocalDate calcularValidadeExame(LocalDate emissaoExame, TipoExame tipoExame) {
        LocalDate dataValidade = emissaoExame.plusMonths(tipoExame.getPeriodicidade());
        return dataValidade;
    }

    public ObservableList<Exame> listarExames() {
        ExameCache.carregarExamesVigentes();
        return ExameCache.todosExames;
    }

    public ObservableList<Exame> listarExamePorVencimento(int diasVencimento) {

        List<Exame> list = ExameCache.todosExames;
        list = list.stream()
                .filter(f-> {
                    LocalDate hoje = LocalDate.now();
                    LocalDate validade = f.getDataValidade();
                    return !validade.isAfter(hoje.plusMonths(diasVencimento));

                })
                .collect(Collectors.toList());

        return FXCollections.observableArrayList(list);
    }

}
