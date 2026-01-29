package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.exception.DbException;
import com.joaonardi.gerenciadorocupacional.model.TipoCertificado;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TipoCertificadoDAO extends BaseDAO {
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    private static final String CADASTRAR = "INSERT INTO tipos_certificado (id, nome, periodicidade) VALUES (NULL, ?, ?)";
    private static final String CONSULTAR = "SELECT * FROM tipos_certificado WHERE id = ?";
    private static final String ALTERAR = "UPDATE tipos_certificado SET nome = ? , periodicidade = ? WHERE id = ?";
    private static final String DELETAR = "DELETE FROM tipos_certificado WHERE id = ?";
    private static final String LISTAR = "SELECT * FROM tipos_certificado";
    private static final String LISTAR_NO_FUNCIONARIO = "SELECT tc.* " +
            "FROM tipos_certificado tc " +
            "WHERE NOT EXISTS ( " +
            "    SELECT 1 FROM certificados c " +
            "    WHERE c.tipo_certificado_id = tc.id " +
            "      AND c.funcionario_id = ? " +
            ")";

    public TipoCertificadoDAO() {
    }

    public void cadastrarTipoCertificado(TipoCertificado tipoCertificado) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(CADASTRAR);
            int i = 1;
            preparedStatement.setString(i++, tipoCertificado.getNome());
            preparedStatement.setInt(i++, tipoCertificado.getPeriodicidade());
            preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
            throw new DbException("Erro ao cadastrar Tipo Certificado", e);
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public TipoCertificado consultarTipoCertificado(int id) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        TipoCertificado tipoCertificado = null;
        try {
            preparedStatement = connection.prepareStatement(CONSULTAR);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                tipoCertificado = TipoCertificado.TipoCertificadoBuilder.builder()
                        .nome(resultSet.getString("nome"))
                        .build();
            }

        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao consultar tipo certificado");
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet, preparedStatement);
        }
        return tipoCertificado;
    }

    public void alterarTipoCertificado(int id, TipoCertificado tipoCertificado) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(ALTERAR);
            int i = 1;
            preparedStatement.setString(i++, tipoCertificado.getNome());
            preparedStatement.setInt(i++, tipoCertificado.getPeriodicidade());
            preparedStatement.setInt(i++, id);

            preparedStatement.executeUpdate();
            connection.commit();

        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e,"Erro ao alterar tipo certificado");
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public void deletarTipoCertificado(int id) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(DELETAR);
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
            connection.commit();

        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e,"Erro ao deletar tipo certificado");
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public ObservableList<TipoCertificado> listarTiposCertificado() {
        Connection connection = DBConexao.getInstance().abrirConexao();
        ObservableList<TipoCertificado> lista = FXCollections.observableArrayList();
        try {
            preparedStatement = connection.prepareStatement(LISTAR);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                TipoCertificado tipoCertificado = TipoCertificado.TipoCertificadoBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .nome(resultSet.getString("nome"))
                        .periodicidade(resultSet.getInt("periodicidade"))
                        .build();
                lista.add(tipoCertificado);
            }
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e,"Erro ao carregar tipos certificados");
        } finally {
            close(resultSet, preparedStatement);
        }
        return lista;
    }

    public ObservableList<TipoCertificado> listarTiposCertificado(Integer idFuncionario) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        ObservableList<TipoCertificado> lista = FXCollections.observableArrayList();
        try {
            preparedStatement = connection.prepareStatement(LISTAR_NO_FUNCIONARIO);
            preparedStatement.setInt(1,idFuncionario);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                TipoCertificado tipoCertificado = TipoCertificado.TipoCertificadoBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .nome(resultSet.getString("nome"))
                        .periodicidade(resultSet.getInt("periodicidade"))
                        .build();
                lista.add(tipoCertificado);
            }
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e,"Erro ao carregar tipos certificados");
        } finally {
            close(resultSet, preparedStatement);
        }
        return lista;
    }
}

