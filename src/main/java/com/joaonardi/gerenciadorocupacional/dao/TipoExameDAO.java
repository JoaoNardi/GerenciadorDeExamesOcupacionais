package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoExameDAO {
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;

    private static final String CADASTRAR = "INSERT INTO tipos_exame (id, nome, periodicidade) VALUES (NULL, ?, ?)";
    private static final String CONSULTAR = "SELECT * FROM tipos_exame WHERE id = ?";
    private static final String ALTERAR = "UPDATE tipos_exame SET nome = ? , periodicidade = ? WHERE id = ?";
    private static final String DELETAR = "DELETE FROM tipos_exame WHERE id = ?";
    private static final String LISTAR = "SELECT * FROM tipos_exame";

    public TipoExameDAO() {}

    public void cadastrarTipoExame(TipoExame tipoExame) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(CADASTRAR);
            int i = 1;
            preparedStatement.setString(i++, tipoExame.getNome());
            preparedStatement.setInt(i++, tipoExame.getPeriodicidade());
            preparedStatement.execute();
            connection.commit();
            JOptionPane.showMessageDialog(null, "Tipo de exame cadastrado com sucesso.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            fecharConexao();
        }
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
            fecharConexao();
        }

        return tipoExame;
    }

    public void alterarTipoExame(TipoExame tipoExame) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(ALTERAR);
            int i = 1;
            preparedStatement.setString(i++, tipoExame.getNome());
            preparedStatement.setInt(i++,tipoExame.getPeriodicidade());
            preparedStatement.setInt(i++, tipoExame.getId());

            int linhasAfetadas = preparedStatement.executeUpdate();
            connection.commit();

            if (linhasAfetadas > 0) {
                JOptionPane.showMessageDialog(null, "Tipo de exame atualizado com sucesso.");
            } else {
                JOptionPane.showMessageDialog(null, "Tipo de exame não encontrado para atualizar.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            fecharConexao();
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
            fecharConexao();
        }
    }

    public List<TipoExame> listarTiposExame() {
        Connection connection = DBConexao.getInstance().abrirConexao();
        List<TipoExame> lista = new ArrayList<>();

        try {
            preparedStatement = connection.prepareStatement(LISTAR);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                TipoExame tipoExame = TipoExame.TipoExameBuilder.builder()
                        .nome(resultSet.getString("nome"))
                        .periodicidade(resultSet.getInt("periodicidade "))
                        .build();
                lista.add(tipoExame);
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
