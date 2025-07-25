package com.joaonardi.gerenciadorocupacional.cache;

import com.joaonardi.gerenciadorocupacional.dao.ExameDAO;
import com.joaonardi.gerenciadorocupacional.model.Exame;
import javafx.collections.ObservableList;

public class ExameCache {
    private static ExameDAO exameDAO = new ExameDAO();
    public static ObservableList<Exame> todosExames;
    public static void carregarExames() {
        todosExames = exameDAO.listarExames();
    }
}
