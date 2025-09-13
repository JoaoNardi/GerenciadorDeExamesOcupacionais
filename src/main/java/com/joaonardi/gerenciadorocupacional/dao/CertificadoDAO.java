package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.model.Certificado;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CertificadoDAO {
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;

    private static final String DRIVER = "org.sqlite.JDBC";
    private static final String BD = "jdbc:sqlite:resources/_db/db_gerenciador.db";

    private static final String CADASTRAR_CERTIFICADO = "INSERT INTO CERTIFICADOS (id, id_tipo_certificado, idFuncionario, dataEmissao, " +
            "dataValidade, atualizado_por" +
            " tempo_validade) VALUES (NULL, ?, ?, ?, ?,?)";
    private static final String CONSULTAR_CERTIFICADO = "SELECT * FROM CERTIFICADOS WHERE id = ?";
    private static final String ALTERAR_CERTIFICADO = "UPDATE CERTIFICADOS SET tipo_certificado_id = ?, funcionario_id = ? , data_emissão = ? ," +
            "atualizado_por = ?" +
            "WHERE id = ?";
    private static final String DELETAR_CERTIFICADO = "DELETE FROM CERTIFICADOS WHERE id = ?";
    private static final String LISTAR_CERTIFICADOS = "SELECT * FROM CERTIFICADOS";

    public CertificadoDAO() {
    }

    public void cadastrarCertificado(Certificado certificado) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(CADASTRAR_CERTIFICADO);
            int i = 1;
            preparedStatement.setInt(i++,certificado.getTipoCertificadoId());
            preparedStatement.setInt(i++, certificado.getFuncionarioId());
            preparedStatement.setDate(i++, Date.valueOf(certificado.getDataEmissao()));
            preparedStatement.setDate(i++, Date.valueOf(certificado.getDataEmissao()));
            preparedStatement.setInt(i++, certificado.getAtualizadoPor());

            preparedStatement.execute();
            connection.commit();

            JOptionPane.showMessageDialog(null, "Certificado cadastrado com sucesso");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet,preparedStatement);
        }
    }

    public Certificado consultarCertificado(int id) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        Certificado certificado = null;
        try {
            preparedStatement = connection.prepareStatement(CONSULTAR_CERTIFICADO);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                certificado = Certificado.CertificadoBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .tipoCertificadoId(resultSet.getInt("tipo_certificado_id"))
                        .funcionarioId(resultSet.getInt("funcionario_id"))
                        .dataEmissao(resultSet.getDate("data_emissao").toLocalDate())
                        .dataValidade(resultSet.getDate("data_validade").toLocalDate())
                        .build();
            } else {
                JOptionPane.showMessageDialog(null, "Certificado não encontrado");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet,preparedStatement);
        }
        return certificado;
    }


    public void alterarCertificado(Certificado certificado) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(ALTERAR_CERTIFICADO);
            int i = 1;
            preparedStatement.setInt(i++, certificado.getTipoCertificadoId());
            preparedStatement.setInt(i++, certificado.getFuncionarioId());
            preparedStatement.setDate(i++, Date.valueOf(certificado.getDataEmissao()));
            preparedStatement.setDate(i++, Date.valueOf(certificado.getDataValidade()));
            preparedStatement.setInt(i++, certificado.getAtualizadoPor());
            preparedStatement.setInt(i++, certificado.getId());

            int linhasAfetadas = preparedStatement.executeUpdate();
            connection.commit();

            if (linhasAfetadas > 0) {
                JOptionPane.showMessageDialog(null, "Certificado atualizado com sucesso");
            } else {
                JOptionPane.showMessageDialog(null, "Certificado não encontrado para atualizar");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet,preparedStatement);
        }
    }

    public void deletarCertificado(int id) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(DELETAR_CERTIFICADO);
            preparedStatement.setInt(1, id);

            int linhasAfetadas = preparedStatement.executeUpdate();
            connection.commit();

            if (linhasAfetadas > 0) {
                JOptionPane.showMessageDialog(null, "Certificado excluído com sucesso");
            } else {
                JOptionPane.showMessageDialog(null, "Certificado não encontrado para exclusão");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet,preparedStatement);
        }
    }

    public List<Certificado> listarCertificados() {
        Connection connection = DBConexao.getInstance().abrirConexao();
        List<Certificado> listaCertificados = new ArrayList<>();

        try {
            preparedStatement = connection.prepareStatement(LISTAR_CERTIFICADOS);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Certificado certificado = Certificado.CertificadoBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .tipoCertificadoId(resultSet.getInt("tipo_certificado_id"))
                        .funcionarioId(resultSet.getInt("funcionario_id"))
                        .dataEmissao(resultSet.getDate("data_emissao").toLocalDate())
                        .dataValidade(resultSet.getDate("data_validade").toLocalDate())
                        .build();
                listaCertificados.add(certificado);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet,preparedStatement);
        }

        return listaCertificados;
    }

}
