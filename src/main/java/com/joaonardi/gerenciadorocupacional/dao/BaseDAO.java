package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.exception.DataDuplicityException;
import com.joaonardi.gerenciadorocupacional.exception.DbException;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public abstract class BaseDAO {
    protected void commit(Connection conn) {
        try {
            conn.commit();
        } catch (SQLException e) {
            throw new DbException("Erro ao realizar operação", e);
        }
    }

    protected void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException e) {
            throw new DbException("Erro ao realizar rollback após falha", e);
        }
    }
    protected void trataSqlExceptions(SQLException e, String contexto){
        String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
        if (msg.contains("unique constraint failed")) {
            throw new DataDuplicityException("Já existe um registro cadastrado com esses dados: " + contexto, e);
        }
        if (msg.contains("foreign key constraint failed")) {
            throw new DbException("Não é possível excluir: existem registros vinculados: " + contexto, e);
        }
        throw new DbException(contexto + ". Erro de banco de dados: " + msg, e);
    }

    protected void close(ResultSet rs, PreparedStatement ps) {
        DBConexao.getInstance().fechaConexao(rs, ps);
    }
}


