package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.exception.DataAccessException;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.RelatorioItem;
import com.joaonardi.gerenciadorocupacional.model.TipoDe;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RelatorioDAO {
    final DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;


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
                        WITH filtros(funcionarioId, inputData, dataInicial, dataFinal, tipoDeId, exame, certificado) AS (
                            VALUES(?, ?, ?, ?, ?, ?, ?)
                        )
                        SELECT e.id, 'Exame' AS origem, e.funcionario_id, e.tipo_exame_id AS tipo_id, e.data_emissao, e.data_validade, e.atualizado_por
                        FROM exames e, filtros f
                        WHERE (f.exame = 1)
                          AND (f.funcionarioId IS NULL OR e.funcionario_id = f.funcionarioId)
                          AND (f.tipoDeId IS NULL OR e.tipo_exame_id = f.tipoDeId)
                          AND (
                              (f.inputData = 'Emissao' AND date(e.data_emissao) BETWEEN date(f.dataInicial) AND date(f.dataFinal))
                              OR
                              (f.inputData = 'Validade' AND date(e.data_validade) BETWEEN date(f.dataInicial) AND date(f.dataFinal))
                          )
                        UNION ALL
                        SELECT c.id, 'Certificado' AS origem, c.funcionario_id, c.tipo_certificado_id AS tipo_id, c.data_emissao, c.data_validade, c.atualizado_por
                        FROM certificados c, filtros f
                        WHERE (f.certificado = 1)
                          AND (f.funcionarioId IS NULL OR c.funcionario_id = f.funcionarioId)
                          AND (f.tipoDeId IS NULL OR c.tipo_certificado_id = f.tipoDeId)
                          AND (
                              (f.inputData = 'Emissao' AND date(c.data_emissao) BETWEEN date(f.dataInicial) AND date(f.dataFinal))
                              OR
                              (f.inputData = 'Validade' AND date(c.data_validade) BETWEEN date(f.dataInicial) AND date(f.dataFinal))
                          )
                        ORDER BY data_emissao;
                    """;
            preparedStatement = connection.prepareStatement(sql);
            int i = 1;
            preparedStatement.setInt(i++,funcionario.getId());
            preparedStatement.setString(i++, inputData);
            preparedStatement.setString(i++, dataInicial.format(formato));
            preparedStatement.setString(i++, dataFinal.format(formato));
            preparedStatement.setObject(i++, tipoDe != null ? tipoDe.getId() : null);
            preparedStatement.setInt(i++, exame ? 1 : 0);
            preparedStatement.setInt(i++, certificado ? 1 : 0);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                RelatorioItem item = RelatorioItem.RelatorioItemBuilder.builder().id(resultSet.getInt("id"))
                        .idFuncionario(resultSet.getInt("funcionario_id"))
                        .origem(resultSet.getString("origem"))
                        .tipoId(resultSet.getInt("tipo_id"))
                        .dataEmissao(LocalDate.parse(resultSet.getString("data_emissao"), formato))
                        .dataValidade(LocalDate.parse(resultSet.getString("data_validade"), formato))
                        .atualizadoPor(resultSet.getObject("atualizado_por") != null ? resultSet.getInt("atualizado_por") : null )
                        .build();
                lista.add(item);
            }
            connection.commit();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new DataAccessException("Erro ao realizar rollback após falha", ex);
            }
            throw new DataAccessException("Erro ao gerar relatorio", e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet, preparedStatement);
        }

        return lista;
    }
}

