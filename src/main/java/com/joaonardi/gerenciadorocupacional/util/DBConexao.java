package com.joaonardi.gerenciadorocupacional.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConexao {
    private static DBConexao instance;
    private static final String DRIVER = "org.sqlite.JDBC";
    private static final String BD = "jdbc:sqlite:src/main/resources/_db/db_gerenciador.db";
    private Connection conexao;

    private DBConexao() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("Driver JDBC não encontrado: " + e.getMessage());
        }
    }

    public static DBConexao getInstance() {
        if (instance == null) {
            instance = new DBConexao();
        }
        return instance;
    }

    public Connection abrirConexao() {
        try {
            if (conexao == null || conexao.isClosed()) {
                conexao = DriverManager.getConnection(BD);
                conexao.setAutoCommit(false);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao conectar com banco de dados: " + e.getMessage());
        }
        return conexao;
    }

    public void fechaConexao() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao fechar conexão com banco de dados: " + e.getMessage());
        } finally {
            conexao = null;
        }
    }
}
