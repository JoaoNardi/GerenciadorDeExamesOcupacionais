package com.joaonardi.gerenciadorocupacional.cache;

import com.joaonardi.gerenciadorocupacional.dao.ExameDAO;
import com.joaonardi.gerenciadorocupacional.model.Exame;
import javafx.collections.ObservableList;

public class ExameCache {
    private static ExameDAO exameDAO = new ExameDAO();
    public static ObservableList<Exame> todosExames;
    public static void carregarExamesTodos() {
        todosExames = exameDAO.listarExamesVigentes(false);
    }
    public static void carregarExamesVigentes() {
        todosExames = exameDAO.listarExamesVigentes(true);
    }
}
