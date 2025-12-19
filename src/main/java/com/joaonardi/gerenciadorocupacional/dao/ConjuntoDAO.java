package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.model.Conjunto;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class ConjuntoDAO extends BaseDAO{
    
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    private static final String CADASTRAR_CONJUNTO = "INSERT INTO conjuntos (id, tipo_exame_id, periodicidade) " +
            "VALUES (NULL, ?, ?)";
    private static final String DELETAR_CONJUNTO = "DELETE FROM conjuntos WHERE id = ?";
    private static final String LISTAR_CONJUNTOS_EXAME_ID = "SELECT c.*, te.id as te_id, te.nome as te_nome " +
            "FROM conjuntos c " +
            "JOIN tipos_exame te on te.id = c.tipo_exame_id " +
            "WHERE tipo_exame_id = ? ";

    public Conjunto cadastrarConjunto(Conjunto conjunto) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(CADASTRAR_CONJUNTO, Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            preparedStatement.setInt(i++, conjunto.getTipoExame().getId());
            preparedStatement.setInt(i++, conjunto.getPeriodicidade());
            preparedStatement.execute();
            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    conjunto.setId(idGerado);
                }
            }
            commit(connection);
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e,"Erro ao cadastrar conjunto de condicoes");
        } finally {
            close(resultSet, preparedStatement);
        }
        return conjunto;
    }

    public void deletarConjunto(int id) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(DELETAR_CONJUNTO);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            commit(connection);
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e,"Erro ao deletar conjunto de condicoes");
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public ObservableList<Conjunto> listarConjuntosPorTipoExameId(int idTipoExame) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        Conjunto conjunto;
        ObservableList<Conjunto> listaConjuntosPorTipoExameId = FXCollections.observableArrayList();
        try {
            preparedStatement = connection.prepareStatement(LISTAR_CONJUNTOS_EXAME_ID);
            preparedStatement.setInt(1, idTipoExame);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                TipoExame tipoExame = TipoExame.TipoExameBuilder.builder()
                        .id(resultSet.getInt("te_id"))
                        .nome(resultSet.getString("te_nome"))
                        .build();

                conjunto = Conjunto.ConjuntoBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .tipoExame(tipoExame)
                        .periodicidade(resultSet.getInt("periodicidade"))
                        .build();
                listaConjuntosPorTipoExameId.add(conjunto);
            }
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao carregar conjunto de condicoes");
        } finally {
            close(resultSet, preparedStatement);
        }
        return listaConjuntosPorTipoExameId;
    }
}
