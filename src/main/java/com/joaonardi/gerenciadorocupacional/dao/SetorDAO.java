package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.model.Setor;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SetorDAO {
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;

    private static final String DRIVER = "org.sqlite.JDBC";
    private static final String BD = "jdbc:sqlite:resources/_db/db_gerenciador.db";

    private static final String CADASTRAR_SETOR = "INSERT INTO SETORES (id, area, grau_risco) VALUES (NULL, ?, ?)";

    private static final String CONSULTAR_SETOR = "SELECT * FROM SETORES WHERE id = ?";

    private static final String ALTERAR_SETOR = "UPDATE SETORES SET area = ?, grau_risco = ? WHERE id = ?";

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
            preparedStatement.setInt(i++, setor.getGrauRisco());

            preparedStatement.execute();
            connection.commit();

            JOptionPane.showMessageDialog(null, "Setor cadastrado com sucesso");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            fecharConexao();
        }
    }

    public Setor consultarSetor(int id) throws Exception {
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
                        .grauRisco(resultSet.getInt("grau_risco"))
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            fecharConexao();
        }

        if (setor == null) {
            JOptionPane.showMessageDialog(null, "Não foi possível localizar o setor selecionado",
                    "", JOptionPane.WARNING_MESSAGE);
            throw new Exception("Não foi possível localizar o setor selecionado");
        }

        return setor;
    }

    public void alterarSetor(int id, Setor setor) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(ALTERAR_SETOR);
            int i = 1;
            preparedStatement.setString(i++, setor.getArea());
            preparedStatement.setInt(i++, setor.getGrauRisco());
            preparedStatement.setInt(i++, id);

            preparedStatement.executeUpdate();
            connection.commit();

            JOptionPane.showMessageDialog(null, "Setor alterado com sucesso");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            fecharConexao();
        }
    }

    public void deletarSetor(int id) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(DELETAR_SETOR);
            preparedStatement.setInt(1, id);

            preparedStatement.execute();
            connection.commit();

            JOptionPane.showMessageDialog(null, "Setor deletado com sucesso");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            fecharConexao();
        }
    }

    public ObservableList<Setor> listarSetores() {
        Connection connection = DBConexao.getInstance().abrirConexao();
        ObservableList<Setor> listaSetores =  FXCollections.observableArrayList();;

        try {
            preparedStatement = connection.prepareStatement(LISTAR_SETORES);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Setor setor = Setor.SetorBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .area(resultSet.getString("area"))
                        .grauRisco(resultSet.getInt("grau_risco"))
                        .build();
                listaSetores.add(setor);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            fecharConexao();
        }

        return listaSetores;
    }

    private void fecharConexao() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            DBConexao.getInstance().fechaConexao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
