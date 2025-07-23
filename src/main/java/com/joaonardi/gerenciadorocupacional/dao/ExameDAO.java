package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.model.Exame;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExameDAO {
    DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;

    private static final String DRIVER = "org.sqlite.JDBC";
    private static final String BD = "jdbc:sqlite:resources/_db/db_gerenciador.db";

    private static final String CADASTRAR_EXAME = "INSERT INTO EXAMES (id, tipo_exame_id, funcionario_id, data_emissao, data_validade, " +
            "atualizado_por)"
            + " VALUES (NULL, ?, ?, ?, ?, ?)";

    private static final String CONSULTAR_EXAME = "SELECT * FROM EXAMES WHERE id = ?";

    private static final String ALTERAR_EXAME = "UPDATE EXAMES SET tipo_exame_id = ?, data_emissao = ?, data_validade = ?, atualizado_por = ? WHERE" +
            " id = ?";

    private static final String DELETAR_EXAME = "DELETE FROM EXAMES WHERE id = ?";

    private static final String LISTAR_EXAMES = "SELECT * FROM EXAMES";

    public ExameDAO() {
    }

    public void cadastrarExame(Exame exame) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(CADASTRAR_EXAME);
            int i = 1;
            preparedStatement.setInt(i++,exame.getIdTipoExame());
            preparedStatement.setInt(i++,exame.getIdFuncionario());
            preparedStatement.setString(i++, exame.getDataEmissao().format(formato));
            preparedStatement.setString(i++, exame.getDataValidade().format(formato));
            preparedStatement.setInt(i++, exame.getAtualizadoPor());

            preparedStatement.execute();
            connection.commit();

            JOptionPane.showMessageDialog(null, "Exame cadastrado com sucesso");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            fecharConexao();
        }
    }
    //daqui pra baixo
    public Exame consultarExame(int id) throws Exception {
        Connection connection = DBConexao.getInstance().abrirConexao();
        Exame exame = null;
        try {
            preparedStatement = connection.prepareStatement(CONSULTAR_EXAME);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                exame = Exame.ExameBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .idTipoExame(resultSet.getInt("id_tipo_exame"))
                        .idFuncionario(resultSet.getInt("idFuncionario"))
                        .dataEmissao(LocalDate.parse(resultSet.getString("data_emissao")))
                        .dataValidade(LocalDate.parse(resultSet.getString("data_validade")))
                        .atualizadoPor(resultSet.getInt("atualizado_por"))
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            fecharConexao();
        }

        if (exame == null) {
            JOptionPane.showMessageDialog(null, "Não foi possível localizar o exame selecionado",
                    "", JOptionPane.WARNING_MESSAGE);
            throw new Exception("Não foi possível localizar o exame selecionado");
        }

        return exame;
    }

    public void alterarExame(int id, Exame exame) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(CADASTRAR_EXAME);
            int i = 1;
            preparedStatement.setInt(i++,exame.getIdTipoExame());
            preparedStatement.setString(i++, exame.getDataEmissao().format(formato));
            preparedStatement.setString(i++, exame.getDataValidade().format(formato));
            preparedStatement.setInt(i++, exame.getAtualizadoPor());
            preparedStatement.setInt(i++, id);

            preparedStatement.executeUpdate();
            connection.commit();

            JOptionPane.showMessageDialog(null, "Exame alterado com sucesso");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            fecharConexao();
        }
    }

    public void deletarExame(int id) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(DELETAR_EXAME);
            preparedStatement.setInt(1, id);

            preparedStatement.execute();
            connection.commit();

            JOptionPane.showMessageDialog(null, "Exame deletado com sucesso");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            fecharConexao();
        }
    }

    public ObservableList<Exame> listarExames() {
        Connection connection = DBConexao.getInstance().abrirConexao();
        ObservableList<Exame> listaExames = FXCollections.observableArrayList();

        try {
            preparedStatement = connection.prepareStatement(LISTAR_EXAMES);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Exame exame = Exame.ExameBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .idTipoExame(resultSet.getInt("tipo_exame_id"))
                        .idFuncionario(resultSet.getInt("funcionario_id"))
                        .dataEmissao(LocalDate.parse(resultSet.getString("data_emissao")))
                        .dataValidade(LocalDate.parse(resultSet.getString("data_validade")))
                        .atualizadoPor(resultSet.getInt("atualizado_por"))
                        .build();
                listaExames.add(exame);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            fecharConexao();
        }

        return listaExames;
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
