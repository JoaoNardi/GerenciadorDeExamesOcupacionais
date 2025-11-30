package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.exception.DbException;
import com.joaonardi.gerenciadorocupacional.model.Conjunto;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class ConjuntoDAO extends BaseDAO{
    
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    private static final String CADASTRAR_CONJUNTO = "INSERT INTO conjuntos (id, tipo_exame_id, periodicidade) " +
            "VALUES (NULL, ?, ?)";
    private static final String CONSULTAR_CONJUNTO = "SELECT * FROM conjuntos WHERE ID = ?";
    private static final String ALTERAR_CONJUNTO = "UPDATE conjuntos SET tipo_exame_id = ?, periodicidade = ? WHERE id = ? ";
    private static final String DELETAR_CONJUNTO = "DELETE FROM conjuntos WHERE id = ?";
    private static final String LISTAR_TODOS_CONJUNTOS = "SELECT * FROM conjuntos";
    private static final String LISTAR_CONJUNTOS_EXAME_ID = "SELECT * FROM conjuntos WHERE tipo_exame_id = ?";

    public Conjunto cadastrarConjunto(Conjunto conjunto) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(CADASTRAR_CONJUNTO, Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            preparedStatement.setInt(i++, conjunto.getTipoExameId());
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
            throw new DbException("Erro ao cadastrar condição", e);
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
            throw new DbException("Erro ao deletar conjunto de condicoes", e);
        } finally {
            close(resultSet, preparedStatement);
        }
    }

//    public ObservableList<Conjunto> listarTodosConjuntos() {
//        Connection connection = DBConexao.getInstance().abrirConexao();
//        Condicao condicao;
//        ObservableList<Condicao> listarCondicoesPorTipoExameId = FXCollections.observableArrayList();
//        try {
//            preparedStatement = connection.prepareStatement(LISTAR_TODAS_CONDICOES);
//            resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                condicao = Condicao.CondicaoBuilder.builder()
//                        .id(resultSet.getInt("id"))
//                        .conjuntoId(resultSet.getInt("conjunto_id"))
//                        .referencia(resultSet.getString("referencia"))
//                        .operador(resultSet.getString("operador"))
//                        .parametro(resultSet.getString("parametro"))
//                        .build();
//                listarCondicoesPorTipoExameId.add(condicao);
//            }
//        } catch (SQLException e) {
//            rollback(connection);
//            throw new DbException("Erro ao carregar condições", e);
//        } finally {
//            close(resultSet, preparedStatement);
//        }
//        return listarCondicoesPorTipoExameId;
//    }

    public ObservableList<Conjunto> listarConjuntosPorTipoExameId(int idTipoExame) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        Conjunto conjunto;
        ObservableList<Conjunto> listaConjuntosPorTipoExameId = FXCollections.observableArrayList();
        try {
            preparedStatement = connection.prepareStatement(LISTAR_CONJUNTOS_EXAME_ID);
            preparedStatement.setInt(1, idTipoExame);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                conjunto = Conjunto.ConjuntoBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .tipoExameId(resultSet.getInt("tipo_exame_id"))
                        .periodicidade(resultSet.getInt("periodicidade"))
                        .build();
                listaConjuntosPorTipoExameId.add(conjunto);
            }
        } catch (SQLException e) {
            rollback(connection);
            throw new DbException("Erro ao carregar conjuntos por tipoexame", e);
        } finally {
            close(resultSet, preparedStatement);
        }
        return listaConjuntosPorTipoExameId;
    }
}
