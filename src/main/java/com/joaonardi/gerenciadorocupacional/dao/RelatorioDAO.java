package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.RelatorioItem;
import com.joaonardi.gerenciadorocupacional.model.Setor;
import com.joaonardi.gerenciadorocupacional.model.TipoDe;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import com.joaonardi.gerenciadorocupacional.util.FormataData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class RelatorioDAO extends BaseDAO {
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public ObservableList<RelatorioItem> montarRelatorio(
            Funcionario funcionario,
            String inputData,
            LocalDate dataInicial,
            LocalDate dataFinal,
            TipoDe tipoDe,
            boolean exame,
            boolean certificado) {
        ObservableList<RelatorioItem> lista = FXCollections.observableArrayList();
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            String sql = """
                    WITH filtros (
                              funcionarioId,
                              inputData,
                              dataInicial,
                              dataFinal,
                              tipoDeId,
                              exame,
                              certificado
                          ) AS (
                              VALUES (?, ?, ?, ?, ?, ?, ?)
                          )
                    
                          SELECT
                              e.id,
                              'Exame' AS origem,
                              e.funcionario_id,
                              e.tipo_exame_id AS tipo_id,
                              e.data_emissao,
                              e.data_validade,
                              e.atualizado_por,
                    
                              fu.id AS fu_id,
                              fu.nome AS fu_nome,
                              fu.cpf AS fu_cpf,
                              fu.data_nascimento AS fu_data_nascimento,
                              fu.data_admissao AS fu_data_admissao,
                              fu.setor_id AS fu_setor_id,
                              fu.ativo AS fu_ativo,
                    
                              s.id AS s_id,
                              s.area AS s_area
                          FROM exames e
                          JOIN funcionarios fu ON fu.id = e.funcionario_id
                          JOIN setores s ON s.id = fu.setor_id
                          JOIN filtros f ON 1 = 1
                          WHERE f.exame = 1
                            AND (f.funcionarioId IS NULL OR e.funcionario_id = f.funcionarioId)
                            AND (f.tipoDeId IS NULL OR e.tipo_exame_id = f.tipoDeId)
                            AND (
                                  (f.inputData = 'Emissao'
                                   AND date(e.data_emissao) BETWEEN date(f.dataInicial) AND date(f.dataFinal))
                                  OR
                                  (f.inputData = 'Validade'
                                   AND date(e.data_validade) BETWEEN date(f.dataInicial) AND date(f.dataFinal))
                                )
                    
                          UNION ALL
                    
                          SELECT
                              c.id,
                              'Certificado' AS origem,
                              c.funcionario_id,
                              c.tipo_certificado_id AS tipo_id,
                              c.data_emissao,
                              c.data_validade,
                              c.atualizado_por,
                    
                              fu.id AS fu_id,
                              fu.nome AS fu_nome,
                              fu.cpf AS fu_cpf,
                              fu.data_nascimento AS fu_data_nascimento,
                              fu.data_admissao AS fu_data_admissao,
                              fu.setor_id AS fu_setor_id,
                              fu.ativo AS fu_ativo,
                    
                              s.id AS s_id,
                              s.area AS s_area
                          FROM certificados c
                          JOIN funcionarios fu ON fu.id = c.funcionario_id
                          JOIN setores s ON s.id = fu.setor_id
                          JOIN filtros f ON 1 = 1
                          WHERE f.certificado = 1
                            AND (f.funcionarioId IS NULL OR c.funcionario_id = f.funcionarioId)
                            AND (f.tipoDeId IS NULL OR c.tipo_certificado_id = f.tipoDeId)
                            AND (
                                  (f.inputData = 'Emissao'
                                   AND date(c.data_emissao) BETWEEN date(f.dataInicial) AND date(f.dataFinal))
                                  OR
                                  (f.inputData = 'Validade'
                                   AND date(c.data_validade) BETWEEN date(f.dataInicial) AND date(f.dataFinal))
                                )
                          ORDER BY 5; -- data_emissao
                    """;
            preparedStatement = connection.prepareStatement(sql);
            int i = 1;
            preparedStatement.setInt(i++, funcionario.getId());
            preparedStatement.setString(i++, inputData);
            preparedStatement.setString(i++, FormataData.iso(dataInicial));
            preparedStatement.setString(i++, FormataData.iso(dataFinal));
            preparedStatement.setObject(i++, tipoDe != null ? tipoDe.getId() : null);
            preparedStatement.setInt(i++, exame ? 1 : 0);
            preparedStatement.setInt(i++, certificado ? 1 : 0);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Setor setor = Setor.SetorBuilder.builder()
                        .id(resultSet.getInt("s_id"))
                        .area(resultSet.getString("s_area"))
                        .build();

                Funcionario funcionario1 = Funcionario.FuncionarioBuilder.builder()
                        .id(resultSet.getInt("fu_id"))
                        .nome(resultSet.getString("fu_nome"))
                        .cpf(resultSet.getString("fu_cpf"))
                        .dataNascimento(LocalDate.parse(resultSet.getString("fu_data_nascimento")))
                        .dataAdmissao(LocalDate.parse(resultSet.getString("fu_data_admissao")))
                        .setor(setor)
                        .build();

                RelatorioItem item = RelatorioItem.RelatorioItemBuilder.builder().id(resultSet.getInt("id"))
                        .funcionario(funcionario1)
                        .origem(resultSet.getString("origem"))
                        .tipoId(resultSet.getInt("tipo_id"))
                        .dataEmissao(LocalDate.parse(resultSet.getString("data_emissao")))
                        .dataValidade(LocalDate.parse(resultSet.getString("data_validade")))
                        .atualizadoPor(resultSet.getObject("atualizado_por") != null ? resultSet.getInt("atualizado_por") : null)
                        .build();
                lista.add(item);
            }
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao gerar relatório");
        } finally {
            close(resultSet, preparedStatement);
        }
        return lista;
    }
}

