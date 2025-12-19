package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.exception.DbException;
import com.joaonardi.gerenciadorocupacional.model.Condicao;
import com.joaonardi.gerenciadorocupacional.model.Conjunto;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CondicaoDAO extends BaseDAO {
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    private static final String CADASTRAR_CONDICAO = "INSERT INTO condicoes (id, conjunto_id, referencia, operador, parametro) " +
            "VALUES (NULL, ?, ?, ?, ?)";
    private static final String CONSULTAR_CONDICAO = "SELECT * FROM condicoes WHERE ID = ?";
    private static final String ALTERAR_CONDICAO = "UPDATE condicoes SET conjunto_id = ?, referencia = ?, operador - ?, parametro = ? WHERE id = ? ";
    private static final String DELETAR_CONDICAO = "DELETE FROM condicoes WHERE id = ?";
    private static final String LISTAR_TODAS_CONDICOES = "SELECT * FROM condicoes";
    private static final String LISTAR_CONDICOES_POR_CONJUNTO_ID = "SELECT cd.*, " +
            "ct.id as ct_id, ct.tipo_exame_id as ct_tipo_exame_id, ct.periodicidade as ct_periodicidade, " +
            "te.id as te_id, te.nome as te_nome " +
            "FROM condicoes as cd " +
            "JOIN tipos_exame te on te.id = ct.tipo_exame_id " +
            "JOIN conjuntos ct on ct.id = cd.conjunto_id " +
            "WHERE conjunto_id = ?";

    public CondicaoDAO() {
    }

    public void cadastrarCondicao(Condicao condicao) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(CADASTRAR_CONDICAO);
            int i = 1;
            preparedStatement.setInt(i++, condicao.getConjunto().getId());
            preparedStatement.setString(i++, condicao.getReferencia());
            preparedStatement.setString(i++, condicao.getOperador());
            preparedStatement.setString(i++, condicao.getParametro());
            preparedStatement.execute();
            commit(connection);
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao cadastrar condicao");
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public void deletarCondicao(int id) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(DELETAR_CONDICAO);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            commit(connection);
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao deletar condicao");
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public void cadastrarListaCondicao(ObservableList<Condicao> lista) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(CADASTRAR_CONDICAO);

            for (Condicao condicao : lista) {
                int i = 1;
                preparedStatement.setInt(i++, condicao.getConjunto().getId());
                preparedStatement.setString(i++, condicao.getReferencia());
                preparedStatement.setString(i++, condicao.getOperador());
                preparedStatement.setString(i++, condicao.getParametro());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            commit(connection);
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao cadastrar lista de condicoes");
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public ObservableList<Condicao> listarCondicoesPorConjuntoId(int idTipoExame) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        Condicao condicao;
        ObservableList<Condicao> listaCondicoesPorTipoExameId = FXCollections.observableArrayList();
        try {
            preparedStatement = connection.prepareStatement(LISTAR_CONDICOES_POR_CONJUNTO_ID);
            preparedStatement.setInt(1, idTipoExame);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                TipoExame tipoExame = TipoExame.TipoExameBuilder.builder()
                        .id(resultSet.getInt("te_id"))
                        .nome(resultSet.getString("te_nome"))
                        .build();

                Conjunto conjunto = Conjunto.ConjuntoBuilder.builder()
                        .id(resultSet.getInt("ct_id"))
                        .tipoExame(tipoExame)
                        .periodicidade(resultSet.getInt("ct_periodicidade"))
                        .build();

                condicao = Condicao.CondicaoBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .conjunto(conjunto)
                        .referencia(resultSet.getString("referencia"))
                        .operador(resultSet.getString("operador"))
                        .parametro(resultSet.getString("parametro"))
                        .build();
                listaCondicoesPorTipoExameId.add(condicao);
            }

        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao carregar condicoes por conjuntoID");
        } finally {
            close(resultSet, preparedStatement);
        }
        return listaCondicoesPorTipoExameId;
    }
}
