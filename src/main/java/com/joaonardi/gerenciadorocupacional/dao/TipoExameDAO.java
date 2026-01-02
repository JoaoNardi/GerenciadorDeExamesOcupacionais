package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.exception.DbException;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class TipoExameDAO extends BaseDAO {
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    private static final String CADASTRAR = "INSERT INTO tipos_exame (id, nome) VALUES (NULL, ?)";
    private static final String CONSULTAR = "SELECT * FROM tipos_exame WHERE id = ?";
    private static final String ALTERAR = "UPDATE tipos_exame SET nome = ? WHERE id = ?";
    private static final String DELETAR = "DELETE FROM tipos_exame WHERE id = ?";
    private static final String LISTAR = "SELECT * FROM tipos_exame";

    public TipoExameDAO() {
    }

    public TipoExame cadastrarTipoExame(TipoExame tipoExame) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(CADASTRAR, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, tipoExame.getNome());
            preparedStatement.execute();

            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    tipoExame.setId(idGerado);
                }
            }
            connection.commit();

        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao cadastrar Tipo Exame");
        } finally {
            close(resultSet, preparedStatement);
        }
        return tipoExame;
    }

    public TipoExame consultarTipoExame(int id) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        TipoExame tipoExame = null;
        try {
            preparedStatement = connection.prepareStatement(CONSULTAR);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                tipoExame = TipoExame.TipoExameBuilder.builder()
                        .nome(resultSet.getString("nome"))
                        .build();
            }

        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e,"Erro ao consultar Tipo Exame");
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet, preparedStatement);
        }
        return tipoExame;
    }

    public void alterarTipoExame(int id, TipoExame tipoExame) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(ALTERAR);
            int i = 1;
            preparedStatement.setString(i++, tipoExame.getNome());
            preparedStatement.setInt(i++, id);
            preparedStatement.executeUpdate();
            connection.commit();

        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e,"Erro ao alterar Tipo Exame");
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public void deletarTipoExame(int id) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(DELETAR);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            connection.commit();

        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e,"Erro ao deletar Tipo Exame");
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public ObservableList<TipoExame> listarTiposExame() {
        Connection connection = DBConexao.getInstance().abrirConexao();
        ObservableList<TipoExame> lista = FXCollections.observableArrayList();

        try {
            preparedStatement = connection.prepareStatement(LISTAR);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                TipoExame tipoExame = TipoExame.TipoExameBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .nome(resultSet.getString("nome"))
                        .build();
                lista.add(tipoExame);
            }
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao carregar Tipo Exames");
        } finally {
            close(resultSet, preparedStatement);
        }
        return lista;
    }
}
