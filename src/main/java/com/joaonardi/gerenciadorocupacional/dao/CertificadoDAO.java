package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.model.Certificado;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.Setor;
import com.joaonardi.gerenciadorocupacional.model.TipoCertificado;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import com.joaonardi.gerenciadorocupacional.util.FormataData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CertificadoDAO extends BaseDAO {
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    private static final String CADASTRAR_CERTIFICADO = "INSERT INTO CERTIFICADOS (id, tipo_certificado_id, funcionario_id, data_emissao, " +
            "data_validade, atualizado_por)" +
            "VALUES (NULL, ?, ?, ?, ?,?)";
    private static final String CONSULTAR_CERTIFICADO = "SELECT * FROM CERTIFICADOS WHERE id = ?";
    private static final String ALTERAR_CERTIFICADO = "UPDATE CERTIFICADOS SET tipo_certificado_id = ?, funcionario_id = ? , data_emissao = ? , " +
            "data_validade = ?," +
            "atualizado_por = ?" +
            "WHERE id = ?";
    private static final String LIMPAR_ATUALIZADO_POR = "UPDATE CERTIFICADOS SET atualizado_por = NULL WHERE atualizado_por = ? ";
    private static final String DELETAR_CERTIFICADO = "DELETE FROM CERTIFICADOS WHERE id = ?";
    private static final String LISTAR_CERTIFICADOS_VIGENTENS = "SELECT c.*, " +
            "tc.id as tc_id, tc.nome as tc_nome, tc.periodicidade as tc_periodicidade, " +
            "f.id as f_id, f.nome as f_nome, f.cpf as f_cpf, f.data_nascimento as f_data_nascimento, " +
            "f.data_admissao as f_data_admissao, f.setor_id as f_setor_id, f.ativo as f_ativo, " +
            "s.id as s_id, s.area as s_area " +
            "FROM CERTIFICADOS c " +
            "JOIN funcionarios f on f.id = c.funcionario_id " +
            "JOIN setores s on s.id = f.setor_id " +
            "JOIN tipos_certificado tc on tc.id = c.tipo_certificado_id " +
            "WHERE c.atualizado_por is NULL ";
    private static final String LISTAR_CERTIFICADOS = "SELECT * FROM CERTIFICADOS";

    public CertificadoDAO() {
    }

    public Certificado cadastrarCertificado(Certificado certificado) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(CADASTRAR_CERTIFICADO, Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            preparedStatement.setInt(i++, certificado.getTipoCertificado().getId());
            preparedStatement.setInt(i++, certificado.getFuncionario().getId());
            preparedStatement.setString(i++, FormataData.iso(certificado.getDataEmissao()));
            preparedStatement.setString(i++, FormataData.iso(certificado.getDataValidade()));
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
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e,"Erro ao cadastrar certificado");
        } finally {
            close(resultSet, preparedStatement);
        }
        return certificado;
    }

    public void alterarCertificado(Certificado certificado) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(ALTERAR_CERTIFICADO);
            int i = 1;
            preparedStatement.setInt(i++, certificado.getTipoCertificado().getId());
            preparedStatement.setInt(i++, certificado.getFuncionario().getId());
            preparedStatement.setString(i++, FormataData.iso(certificado.getDataEmissao()));
            preparedStatement.setString(i++, FormataData.iso(certificado.getDataValidade()));
            if (certificado.getAtualizadoPor() == null) {
                preparedStatement.setObject(i++, null);
            } else {
                preparedStatement.setInt(i++, certificado.getAtualizadoPor());
            }
            preparedStatement.setInt(i++, certificado.getId());

            preparedStatement.executeUpdate();
            int linhasAfetadas = preparedStatement.executeUpdate();
            commit(connection);

            if (linhasAfetadas == 0) {
                connection.rollback();
            }
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao alterar certificado");
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public void deletarCertificado(int id) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(LIMPAR_ATUALIZADO_POR);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement(DELETAR_CERTIFICADO);
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
            connection.commit();

        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao deletar certificado");
        } finally {
            close(resultSet, preparedStatement);
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
                TipoCertificado tipoCertificado = TipoCertificado.TipoCertificadoBuilder.builder()
                        .id(resultSet.getInt("tc_id"))
                        .nome(resultSet.getString("tc_nome"))
                        .periodicidade(resultSet.getInt("tc_periodicidade"))
                        .build();

                Setor setor = Setor.SetorBuilder.builder()
                        .id(resultSet.getInt("s_id"))
                        .area(resultSet.getString("s_area"))
                        .build();

                Funcionario funcionario = Funcionario.FuncionarioBuilder.builder()
                        .id(resultSet.getInt("f_id"))
                        .nome(resultSet.getString("f_nome"))
                        .cpf(resultSet.getString("f_cpf"))
                        .dataNascimento(LocalDate.parse(resultSet.getString("f_data_nascimento")))
                        .dataAdmissao(LocalDate.parse(resultSet.getString("f_data_admissao")))
                        .setor(setor)
                        .build();

                Certificado certificado = Certificado.CertificadoBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .tipoCertificado(tipoCertificado)
                        .funcionario(funcionario)
                        .dataEmissao(LocalDate.parse(resultSet.getString("data_emissao")))
                        .dataValidade(resultSet.getString("data_validade") != null ?
                                LocalDate.parse(resultSet.getString("data_validade")) : null)
                        .atualizadoPor(resultSet.getInt("atualizado_por"))
                        .build();
                listaCertificados.add(certificado);
            }

        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao carregar certificados");
        } finally {
            close(resultSet, preparedStatement);
        }
        return listaCertificados;
    }
}
