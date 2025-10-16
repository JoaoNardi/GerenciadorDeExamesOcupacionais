package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.exception.DbException;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    protected void close(ResultSet rs, PreparedStatement ps) {
        DBConexao.getInstance().fechaConexao(rs, ps);
    }
}


