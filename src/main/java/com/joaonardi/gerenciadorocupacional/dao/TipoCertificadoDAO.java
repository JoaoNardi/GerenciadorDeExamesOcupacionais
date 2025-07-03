package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.model.TipoCertificado;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TipoCertificadoDAO {
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;

    private static final String CADASTRAR = "INSERT INTO tipos_certificado (id, nome, periodicidade) VALUES (NULL, ?, ?)";
    private static final String CONSULTAR = "SELECT * FROM tipos_certificado WHERE id = ?";
    private static final String ALTERAR = "UPDATE tipos_certificado SET nome = ? , periodicidade = ? WHERE id = ?";
    private static final String DELETAR = "DELETE FROM tipos_certificado WHERE id = ?";
    private static final String LISTAR = "SELECT * FROM tipos_certificado";

    public TipoCertificadoDAO() {}

    public void cadastrarTipoCertificado(TipoCertificado tipoCertificado) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(CADASTRAR);
            int i = 1;
            preparedStatement.setString(i++, tipoCertificado.getNome());
            preparedStatement.setInt(i++, tipoCertificado.getPeriodicidade());
            preparedStatement.execute();
            connection.commit();
            JOptionPane.showMessageDialog(null, "Tipo de certificado cadastrado com sucesso.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            fecharConexao();
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
            } else {
                JOptionPane.showMessageDialog(null, "Tipo de certificado não encontrado.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            fecharConexao();
        }

        return tipoCertificado;
    }

    public void alterarTipoCertificado(int id,TipoCertificado tipoCertificado) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(ALTERAR);
            int i = 1;
            preparedStatement.setString(i++, tipoCertificado.getNome());
            preparedStatement.setInt(i++,tipoCertificado.getPeriodicidade());
            preparedStatement.setInt(i++, id);

            int linhasAfetadas = preparedStatement.executeUpdate();
            connection.commit();

            if (linhasAfetadas > 0) {
                JOptionPane.showMessageDialog(null, "Tipo de certificado atualizado com sucesso.");
            } else {
                JOptionPane.showMessageDialog(null, "Tipo de certificado não encontrado para atualizar.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            fecharConexao();
        }
    }

    public void deletarTipoCertificado(int id) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(DELETAR);
            preparedStatement.setInt(1, id);

            int linhasAfetadas = preparedStatement.executeUpdate();
            connection.commit();

            if (linhasAfetadas > 0) {
                JOptionPane.showMessageDialog(null, "Tipo de certificado excluído com sucesso.");
            } else {
                JOptionPane.showMessageDialog(null, "Tipo de certificado não encontrado para exclusão.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            fecharConexao();
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
            throw new RuntimeException(e);
        } finally {
            fecharConexao();
        }

        return lista;
    }

    private void fecharConexao() {
        try {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            DBConexao.getInstance().fechaConexao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

