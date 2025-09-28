package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.model.Certificado;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CertificadoDAO {
    DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;

    private static final String DRIVER = "org.sqlite.JDBC";
    private static final String BD = "jdbc:sqlite:resources/_db/db_gerenciador.db";

    private static final String CADASTRAR_CERTIFICADO = "INSERT INTO CERTIFICADOS (id, tipo_certificado_id, funcionario_id, data_emissao, " +
            "data_validade, atualizado_por)" +
            "VALUES (NULL, ?, ?, ?, ?,?)";
    private static final String CONSULTAR_CERTIFICADO = "SELECT * FROM CERTIFICADOS WHERE id = ?";
    private static final String ALTERAR_CERTIFICADO = "UPDATE CERTIFICADOS SET tipo_certificado_id = ?, funcionario_id = ? , data_emissao = ? , " +
            "data_validade = ?," +
            "atualizado_por = ?" +
            "WHERE id = ?";
    private static final String DELETAR_CERTIFICADO = "DELETE FROM CERTIFICADOS WHERE id = ?";
    private static final String LISTAR_CERTIFICADOS_VIGENTENS = "SELECT * FROM CERTIFICADOS WHERE atualizado_por is NULL";
    private static final String LISTAR_CERTIFICADOS = "SELECT * FROM CERTIFICADOS";

    public CertificadoDAO() {
    }

    public Certificado cadastrarCertificado(Certificado certificado) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(CADASTRAR_CERTIFICADO, Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            preparedStatement.setInt(i++, certificado.getIdTipoCertificado());
            preparedStatement.setInt(i++, certificado.getIdFuncionario());
            preparedStatement.setString(i++, certificado.getDataEmissao().format(formato));
            preparedStatement.setString(i++, certificado.getDataValidade() == null ? null : certificado.getDataValidade().format(formato));
            if (certificado.getAtualizadoPor() == null) {
                preparedStatement.setObject(i++, null, Types.INTEGER);
            } else {
                preparedStatement.setInt(i++,
                        certificado.getAtualizadoPor());
            }

            preparedStatement.execute();
            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    certificado.setId(idGerado);
                }
            }
            connection.commit();

            JOptionPane.showMessageDialog(null, "Certificado cadastrado com sucesso");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet, preparedStatement);
        }
        return certificado;
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
                        .idTipoCertificado(resultSet.getInt("tipo_certificado_id"))
                        .idFuncionario(resultSet.getInt("funcionario_id"))
                        .dataEmissao(resultSet.getDate("data_emissao").toLocalDate())
                        .dataValidade(resultSet.getDate("data_validade").toLocalDate())
                        .build();
            } else {
                JOptionPane.showMessageDialog(null, "Certificado não encontrado");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet, preparedStatement);
        }
        return certificado;
    }


    public void alterarCertificado(Certificado certificado) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(ALTERAR_CERTIFICADO);
            int i = 1;
            preparedStatement.setInt(i++, certificado.getIdTipoCertificado());
            preparedStatement.setInt(i++, certificado.getIdFuncionario());
            preparedStatement.setString(i++, certificado.getDataEmissao().format(formato));
            preparedStatement.setString(i++, (certificado.getDataValidade() == null ? null : certificado.getDataValidade().format(formato)));
            if (certificado.getAtualizadoPor() == null) {
                preparedStatement.setObject(i++, null);
            } else {
                preparedStatement.setInt(i++, certificado.getAtualizadoPor());
            }
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
            DBConexao.getInstance().fechaConexao(resultSet, preparedStatement);
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
            DBConexao.getInstance().fechaConexao(resultSet, preparedStatement);
        }
    }

    public ObservableList<Certificado> listarCertificadosVigentes(boolean inVigentes) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        ObservableList<Certificado> listaCertificados = FXCollections.observableArrayList();

        try {
            if (inVigentes) {
                preparedStatement = connection.prepareStatement(LISTAR_CERTIFICADOS_VIGENTENS);
            }
            if (!inVigentes) {
                preparedStatement = connection.prepareStatement(LISTAR_CERTIFICADOS);
            }
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Certificado certificado = Certificado.CertificadoBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .idTipoCertificado(resultSet.getInt("tipo_certificado_id"))
                        .idFuncionario(resultSet.getInt("funcionario_id"))
                        .dataEmissao(LocalDate.parse(resultSet.getString("data_emissao")))
                        .dataValidade(resultSet.getString("data_validade") != null ?
                                LocalDate.parse(resultSet.getString("data_validade")) : null)
                        .atualizadoPor(resultSet.getInt("atualizado_por"))
                        .build();
                listaCertificados.add(certificado);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet, preparedStatement);
        }

        return listaCertificados;
    }

}
