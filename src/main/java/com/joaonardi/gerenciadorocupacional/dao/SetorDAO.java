package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.exception.DbException;
import com.joaonardi.gerenciadorocupacional.model.Setor;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class SetorDAO extends BaseDAO {
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    private static final String CADASTRAR_SETOR = "INSERT INTO SETORES (id, area) VALUES (NULL, ?)";
    private static final String CONSULTAR_SETOR = "SELECT * FROM SETORES WHERE id = ?";
    private static final String ALTERAR_SETOR = "UPDATE SETORES SET area = ? WHERE id = ?";
    private static final String DELETAR_SETOR = "DELETE FROM SETORES WHERE id = ?";
    private static final String LISTAR_SETORES = "SELECT * FROM SETORES";

    public SetorDAO() {
    }

    public void cadastrarSetor(Setor setor) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(CADASTRAR_SETOR);
            int i = 1;
            preparedStatement.setString(i++, setor.getArea());

            preparedStatement.execute();
            connection.commit();

        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao cadastrar setor");
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public Setor consultarSetor(int id) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        Setor setor = null;
        try {
            preparedStatement = connection.prepareStatement(CONSULTAR_SETOR);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                setor = Setor.SetorBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .area(resultSet.getString("area"))
                        .build();
            }
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao consultar setor");
        } finally {
            close(resultSet, preparedStatement);
        }
        return setor;
    }

    public void alterarSetor(int id, Setor setor) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(ALTERAR_SETOR);
            int i = 1;
            preparedStatement.setString(i++, setor.getArea());
            preparedStatement.setInt(i++, id);

            preparedStatement.executeUpdate();
            connection.commit();

        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao alterar setor");
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public void deletarSetor(int id) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(DELETAR_SETOR);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e,"Erro ao deletar setor");
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet, preparedStatement);
        }
    }

    public ObservableList<Setor> listarSetores() {
        Connection connection = DBConexao.getInstance().abrirConexao();
        ObservableList<Setor> listaSetores = FXCollections.observableArrayList();

        try {
            preparedStatement = connection.prepareStatement(LISTAR_SETORES);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Setor setor = Setor.SetorBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .area(resultSet.getString("area"))
                        .build();
                listaSetores.add(setor);
            }
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e,"Erro ao carregar setores");
        } finally {
            close(resultSet, preparedStatement);
        }
        return listaSetores;
    }
}
