package com.joaonardi.gerenciadorocupacional.util;

import com.joaonardi.gerenciadorocupacional.exception.DbException;

import java.sql.*;

public class DBConexao {
    private static DBConexao instance;
    private static final String DRIVER = "org.sqlite.JDBC";
    private static final String BD = "jdbc:sqlite:src/main/resources/_db/db_gerenciador.db?foreign_keys=on";

    private Connection conexao;

    private DBConexao() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new DbException("Erro ao criar conexão com banco de dados", e);
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
            try (Statement st = conexao.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON;");
            }

        } catch (SQLException e) {
            throw new DbException("Erro ao acessar banco de dados", e);
        }
        return conexao;
    }

    public void fechaConexao(ResultSet resultSet, PreparedStatement preparedStatement) {
        try {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();

            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
            }
        } catch (SQLException e) {
            throw new DbException("Erro ao fechar conexão com o banco de dados", e);
        } finally {
            conexao = null;
        }
    }
}
