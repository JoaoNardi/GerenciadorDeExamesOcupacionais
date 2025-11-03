package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.exception.DbException;
import com.joaonardi.gerenciadorocupacional.model.Exame;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ExameDAO extends BaseDAO {
    final DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    private static final String CADASTRAR_EXAME = "INSERT INTO EXAMES (id, tipo_exame_id, funcionario_id, data_emissao, data_validade, " +
            "atualizado_por)"
            + " VALUES (NULL, ?, ?, ?, ?, ?)";

    private static final String CONSULTAR_EXAME = "SELECT * FROM EXAMES WHERE id = ?";

    private static final String ALTERAR_EXAME = "UPDATE EXAMES SET tipo_exame_id = ?, data_emissao = ?, data_validade = ?, atualizado_por = ? WHERE" +
            " id = ?";
    private static final String LIMPAR_ATUALIZADO_POR = "UPDATE EXAMES SET atualizado_por = NULL WHERE atualizado_por = ? ";
    private static final String DELETAR_EXAME = "DELETE FROM EXAMES WHERE id = ?";
    private static final String LISTAR_EXAMES_VIGENTES = "SELECT * FROM EXAMES WHERE atualizado_por is NULL";
    private static final String LISTAR_TODOS_EXAME = "SELECT * FROM EXAMES";

    public ExameDAO() {
    }

    public Exame cadastrarExame(Exame exame) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(CADASTRAR_EXAME, Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            preparedStatement.setInt(i++, exame.getIdTipoExame());
            preparedStatement.setInt(i++, exame.getIdFuncionario());
            preparedStatement.setString(i++, exame.getDataEmissao().format(formato));
            preparedStatement.setString(i++, exame.getDataValidade() == null ? null : exame.getDataValidade().format(formato));
            if (exame.getAtualizadoPor() == null) {
                preparedStatement.setObject(i++, null, Types.INTEGER);
            } else {
                preparedStatement.setInt(i++,
                        exame.getAtualizadoPor());
            }
            preparedStatement.execute();
            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    exame.setId(idGerado);
                }
            }
            commit(connection);
        } catch (SQLException e) {
            rollback(connection);
            throw new DbException("Erro ao cadastrar exame", e);
        } finally {
            close(resultSet, preparedStatement);
        }
        return exame;
    }

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
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new DbException("Erro ao realizar rollback após falha", ex);
            }
            throw new DbException("Erro ao consultar exame", e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet, preparedStatement);
        }
        return exame;
    }

    public void alterarExame(int id, Exame exame) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(ALTERAR_EXAME);
            int i = 1;
            preparedStatement.setInt(i++, exame.getIdTipoExame());
            preparedStatement.setString(i++, exame.getDataEmissao().format(formato));
            preparedStatement.setString(i++, (exame.getDataValidade() == null ? null : exame.getDataValidade().format(formato)));
            if (exame.getAtualizadoPor() == null) {
                preparedStatement.setObject(i++, null, Types.INTEGER);
            } else {
                preparedStatement.setInt(i++,
                        exame.getAtualizadoPor());
            }
            preparedStatement.setInt(i++, id);

            int linhasAfetadas = preparedStatement.executeUpdate();
            commit(connection);

            if (linhasAfetadas == 0) {
                connection.rollback();
            }
        } catch (SQLException e) {
            rollback(connection);
            throw new DbException("Erro ao alterar exame", e);
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public void deletarExame(int id) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(LIMPAR_ATUALIZADO_POR);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            preparedStatement = null;

            preparedStatement = connection.prepareStatement(DELETAR_EXAME);
            preparedStatement.setInt(1, id);

            int linhasAfetadas = preparedStatement.executeUpdate();

            if (linhasAfetadas == 0) {
                connection.rollback();
            }
            commit(connection);
        } catch (SQLException e) {
            rollback(connection);
            throw new DbException("Erro ao deletar exame", e);
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public ObservableList<Exame> listarExamesVigentes(boolean inVigentes) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        ObservableList<Exame> listaExames = FXCollections.observableArrayList();
        try {
            if (inVigentes) {
                preparedStatement = connection.prepareStatement(LISTAR_EXAMES_VIGENTES);
            }
            if (!inVigentes) {
                preparedStatement = connection.prepareStatement(LISTAR_TODOS_EXAME);
            }
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Exame exame = Exame.ExameBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .idTipoExame(resultSet.getInt("tipo_exame_id"))
                        .idFuncionario(resultSet.getInt("funcionario_id"))
                        .dataEmissao(LocalDate.parse(resultSet.getString("data_emissao")))
                        .dataValidade(resultSet.getString("data_validade") != null ?
                                LocalDate.parse(resultSet.getString("data_validade")) : null)
                        .atualizadoPor(resultSet.wasNull() ? null : resultSet.getInt("atualizado_por"))
                        .build();
                listaExames.add(exame);
            }
        } catch (SQLException e) {
            rollback(connection);
            throw new DbException("Erro ao carregar exames", e);
        } finally {
            close(resultSet, preparedStatement);
        }
        return listaExames;
    }
}
