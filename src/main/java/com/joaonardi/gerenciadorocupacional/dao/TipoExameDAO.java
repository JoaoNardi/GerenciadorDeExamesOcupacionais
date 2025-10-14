package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.*;
import java.sql.*;

public class TipoExameDAO {
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;

    private static final String CADASTRAR = "INSERT INTO tipos_exame (id, nome, periodicidade) VALUES (NULL, ?, ?)";
    private static final String CONSULTAR = "SELECT * FROM tipos_exame WHERE id = ?";
    private static final String ALTERAR = "UPDATE tipos_exame SET nome = ? , periodicidade = ? WHERE id = ?";
    private static final String DELETAR = "DELETE FROM tipos_exame WHERE id = ?";
    private static final String LISTAR = "SELECT * FROM tipos_exame";

    public TipoExameDAO() {}

    public TipoExame cadastrarTipoExame(TipoExame tipoExame) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(CADASTRAR,Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            preparedStatement.setString(i++, tipoExame.getNome());
            preparedStatement.setInt(i++, tipoExame.getPeriodicidade());
            preparedStatement.execute();

            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    tipoExame.setId(idGerado);
                }
            }
            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet,preparedStatement);
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
            } else {
                JOptionPane.showMessageDialog(null, "Tipo de exame não encontrado.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet,preparedStatement);
        }

        return tipoExame;
    }

    public void alterarTipoExame(int id,TipoExame tipoExame) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(ALTERAR);
            int i = 1;
            preparedStatement.setString(i++, tipoExame.getNome());
            preparedStatement.setInt(i++,tipoExame.getPeriodicidade());
            preparedStatement.setInt(i++, id);

            preparedStatement.executeUpdate();
            connection.commit();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet,preparedStatement);
        }
    }

    public void deletarTipoExame(int id) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(DELETAR);
            preparedStatement.setInt(1, id);

            int linhasAfetadas = preparedStatement.executeUpdate();
            connection.commit();

            if (linhasAfetadas > 0) {
                JOptionPane.showMessageDialog(null, "Tipo de exame excluído com sucesso.");
            } else {
                JOptionPane.showMessageDialog(null, "Tipo de exame não encontrado para exclusão.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet,preparedStatement);
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
                        .periodicidade(resultSet.getInt("periodicidade"))
                        .build();
                lista.add(tipoExame);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet,preparedStatement);
        }

        return lista;
    }


}
