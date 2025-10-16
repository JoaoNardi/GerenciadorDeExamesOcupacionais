package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.exception.DataNotFoundException;
import com.joaonardi.gerenciadorocupacional.exception.DbException;
import com.joaonardi.gerenciadorocupacional.model.Condicao;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CondicaoDAO extends BaseDAO {
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    private static final String CADASTRAR_CONDICAO = "INSERT INTO condicoes (id, tipo_exame_id, referencia, operador, parametro, periodicidade) " +
            "VALUES (NULL, ?, ?, ?, ?, ?)";
    private static final String CONSULTAR_CONDICAO = "SELECT * FROM condicoes WHERE ID = ?";
    private static final String ALTERAR_CONDICAO = "UPDATE condicoes SET tipo_exame_id = ?, referencia = ?, operador - ?, parametro = ?, " +
            "periodicidade = ? WHERE id = ? ";
    private static final String DELETAR_CONDICAO = "DELETE FROM condicoes WHERE id = ?";
    private static final String LISTAR_TODAS_CONDICOES = "SELECT * FROM condicoes";
    private static final String LISTAR_CONDICOES_POR_TIPO_EXAME_ID = "SELECT * FROM condicoes WHERE tipo_exame_id = ?";

    public CondicaoDAO() {
    }

    public void cadastrarCondicao(Condicao condicao) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(CADASTRAR_CONDICAO);
            int i = 1;
            preparedStatement.setInt(i++, condicao.getTipoExameId());
            preparedStatement.setString(i++, condicao.getReferencia());
            preparedStatement.setString(i++, condicao.getOperador());
            preparedStatement.setString(i++, condicao.getParametro());
            preparedStatement.setInt(i++, condicao.getPeriodicidade());
            preparedStatement.execute();
            commit(connection);
        } catch (SQLException e) {
            rollback(connection);
            throw new DbException("Erro ao cadastrar condição", e);
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public void alterarCondicao(int id, Condicao condicao) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(ALTERAR_CONDICAO);
            int i = 1;
            preparedStatement.setInt(i++, condicao.getTipoExameId());
            preparedStatement.setString(i++, condicao.getReferencia());
            preparedStatement.setString(i++, condicao.getOperador());
            preparedStatement.setString(i++, condicao.getParametro());
            preparedStatement.setInt(i++, condicao.getPeriodicidade());
            preparedStatement.setInt(i++, id);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new DbException("Erro ao realizar rollback após falha", ex);
            }
            throw new DbException("Erro ao alterar condição", e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet, preparedStatement);
        }
    }

    public void deletarCondicao(int id) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(DELETAR_CONDICAO);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            commit(connection);
        } catch (SQLException e) {
            rollback(connection);
            throw new DbException("Erro ao deletar condicao", e);
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public void cadastrarListaCondicao(ObservableList<Condicao> lista) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(CADASTRAR_CONDICAO);

            for (Condicao condicao : lista) {
                int i = 1;
                preparedStatement.setInt(i++, condicao.getTipoExameId());
                preparedStatement.setString(i++, condicao.getReferencia());
                preparedStatement.setString(i++, condicao.getOperador());
                preparedStatement.setString(i++, condicao.getParametro());
                preparedStatement.setInt(i++, condicao.getPeriodicidade());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            commit(connection);
        } catch (SQLException e) {
            rollback(connection);
            throw new DbException("Erro ao cadastrar lista de condições", e);
        } finally {
            close(resultSet, preparedStatement);
        }
    }


    public ObservableList<Condicao> listarTodasCondicoes() {
        Connection connection = DBConexao.getInstance().abrirConexao();
        Condicao condicao;
        ObservableList<Condicao> listarCondicoesPorTipoExameId = FXCollections.observableArrayList();
        try {
            preparedStatement = connection.prepareStatement(LISTAR_TODAS_CONDICOES);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                condicao = Condicao.CondicaoBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .tipoExameId(resultSet.getInt("tipo_exame_id"))
                        .referencia(resultSet.getString("referencia"))
                        .operador(resultSet.getString("operador"))
                        .parametro(resultSet.getString("parametro"))
                        .periodicidade(resultSet.getInt("periodicidade"))
                        .build();
                listarCondicoesPorTipoExameId.add(condicao);
            }
            if (listarCondicoesPorTipoExameId.isEmpty()) {
                throw new DataNotFoundException("Nenhum exame encontrado.");
            }
        } catch (SQLException e) {
            rollback(connection);
            throw new DbException("Erro ao carregar condições", e);
        } finally {
            close(resultSet, preparedStatement);
        }
        return listarCondicoesPorTipoExameId;
    }

    public ObservableList<Condicao> listarCondicoesPorTipoExameId(int idTipoExame) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        Condicao condicao;
        ObservableList<Condicao> listaCondicoesPorTipoExameId = FXCollections.observableArrayList();
        try {
            preparedStatement = connection.prepareStatement(LISTAR_CONDICOES_POR_TIPO_EXAME_ID);
            preparedStatement.setInt(1, idTipoExame);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                condicao = Condicao.CondicaoBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .tipoExameId(resultSet.getInt("tipo_exame_id"))
                        .referencia(resultSet.getString("referencia"))
                        .operador(resultSet.getString("operador"))
                        .parametro(resultSet.getString("parametro"))
                        .periodicidade(resultSet.getInt("periodicidade"))
                        .build();
                listaCondicoesPorTipoExameId.add(condicao);
            }
            if (listaCondicoesPorTipoExameId.isEmpty()) {
                throw new DataNotFoundException("Nenhuma condição encontrado.");
            }
        } catch (SQLException e) {
            rollback(connection);
            throw new DbException("Erro ao carregar condições por id", e);
        } finally {
            close(resultSet, preparedStatement);
        }
        return listaCondicoesPorTipoExameId;
    }
}
