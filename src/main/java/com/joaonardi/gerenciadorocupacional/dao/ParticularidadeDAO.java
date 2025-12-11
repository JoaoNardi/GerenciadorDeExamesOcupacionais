package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.Particularidade;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public class ParticularidadeDAO extends BaseDAO {
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    //tabela descritiva
    private static final String CADASTRAR_PARTICULARIDADE = "INSERT INTO particularidades (id, nome, descricao, vinculo_tipoDe_id, periodicidade) " +
            "VALUES (NULL, ?, ?, ?, ?)";
    private static final String CONSULTAR_PARTICULARIDADE = "SELECT * FROM particularidades WHERE ID = ?";
    private static final String ALTERAR_PARTICULARIDADE = "UPDATE particularidades SET nome = ?, descricao = ?, vinculo_tipoDe_id = ?, " +
            "periodicidade = ? WHERE id = ? ";
    private static final String DELETAR_PARTICULARIDADE = "DELETE FROM particularidades WHERE id = ?";
    private static final String LISTAR_TODOS_PARTICULARIDADES = "SELECT * FROM particularidades";
    private static final String LISTAR_PARTICULARIDADES_TIPO_EXAME_ID = "SELECT * FROM particularidades WHERE tipo_exame_id = ?";

    //tabela de vinculos
    private static final String VINCULAR_PARTICULARIDADE_FUNCIONARIO = "INSERT INTO vinculos_particularidades (id, funcionario_id, " +
            "particularidade_id) VALUES (NULL, ?, ?)";
    private static final String LISTAR_FUNCIONARIOS_VINCULADOS_PARTICULARIDADE = "SELECT f.*" +
            "FROM vinculos_particularidades vp" +
            "JOIN funcionarios f ON f.id = vp.funcionario_id" +
            "WHERE vp.particularidade_id = ?;";
    private static final String LISTAR_PARTICULARIDADES_VINCULADOS_FUNCIONARIO = "SELECT p.*" +
            "FROM vinculos_particularidades vp" +
            "JOIN particularidade p ON p.id = vp.particularidade_id" +
            "WHERE vp.funcionario_id = ?;";
    private static final String DESVINCULAR_PARTICULARIDADE_FUNCIONARIO = "DELETE FROM vinculos_particularidades WHERE particularidade_id = ? AND " +
            "WHERE funcionario_id = ?  ";

    public Particularidade cadastrarParticularidade(Particularidade particularidade) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(CADASTRAR_PARTICULARIDADE, Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            preparedStatement.setString(i++, particularidade.getNome());
            preparedStatement.setString(i++, particularidade.getDescricao());
            preparedStatement.setInt(i++, particularidade.getTipoExameId());
            preparedStatement.setInt(i++, particularidade.getPeriodicidade());
            preparedStatement.execute();
            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    particularidade.setId(idGerado);
                }
            }
            commit(connection);
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao cadastrar particularidade de condicoes");
        } finally {
            close(resultSet, preparedStatement);
        }
        return particularidade;
    }

    public ObservableList<Particularidade> listarParticularidadesPorTipoExameId(TipoExame tipoExame){
        Connection connection = DBConexao.getInstance().abrirConexao();
        Particularidade particularidade;
        ObservableList<Particularidade> particularidadeList = FXCollections.observableArrayList();
        try {
            preparedStatement = connection.prepareStatement(LISTAR_PARTICULARIDADES_TIPO_EXAME_ID);
            preparedStatement.setInt(1, tipoExame.getId());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                particularidade = Particularidade.ParticularidadeBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .nome(resultSet.getString("nome"))
                        .descricao(resultSet.getString("descricao"))
                        .tipoExameID(resultSet.getInt("tipo_exame_id"))
                        .periodicidade(resultSet.getInt("periodicidade"))
                        .build();
                particularidadeList.add(particularidade);
            }
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao carregar particularidades do funcionário");
        } finally {
            close(resultSet, preparedStatement);
        }
        return particularidadeList;
    }

    public void vincularParticularidadeFuncionario(Funcionario funcionario, Particularidade particularidade) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(VINCULAR_PARTICULARIDADE_FUNCIONARIO);
            int i = 1;
            preparedStatement.setInt(i++, funcionario.getId());
            preparedStatement.setInt(i++, particularidade.getId());
            preparedStatement.execute();
            commit(connection);
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao vincular particularidade - funcionario");
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public void desvincularParticularidadesFuncionario(Particularidade particularidade, Funcionario funcionario) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(DESVINCULAR_PARTICULARIDADE_FUNCIONARIO);
            preparedStatement.setInt(1, particularidade.getId());
            preparedStatement.setInt(2, funcionario.getId());
            preparedStatement.execute();
            commit(connection);
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao desnvincular particularidade - funcionário");
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public ObservableList<Funcionario> listaFuncionariosPorParticularidade(Particularidade particularidade) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        Funcionario funcionario;
        ObservableList<Funcionario> funcionariosList = FXCollections.observableArrayList();
        try {
            preparedStatement = connection.prepareStatement(LISTAR_FUNCIONARIOS_VINCULADOS_PARTICULARIDADE);
            preparedStatement.setInt(1, particularidade.getId());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                funcionario = Funcionario.FuncionarioBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .nome(resultSet.getString("nome"))
                        .cpf(resultSet.getString("cpf"))
                        .dataNascimento(LocalDate.parse(resultSet.getString("data_nascimento")))
                        .dataAdmissao(LocalDate.parse(resultSet.getString("data_admissao")))
                        .idSetor(resultSet.getInt("setor_id"))
                        .ativo(resultSet.getBoolean("ativo"))
                        .build();
                funcionariosList.add(funcionario);

            }
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao carregar funcionario vinculados a particularidade");
        } finally {
            close(resultSet, preparedStatement);
        }
        return funcionariosList;
    }

    public ObservableList<Particularidade> listarParticularidadePorFuncionario(Funcionario funcionario) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        Particularidade particularidade;
        ObservableList<Particularidade> particularidadeList = FXCollections.observableArrayList();
        try {
            preparedStatement = connection.prepareStatement(LISTAR_PARTICULARIDADES_VINCULADOS_FUNCIONARIO);
            preparedStatement.setInt(1, funcionario.getId());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                particularidade = Particularidade.ParticularidadeBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .nome(resultSet.getString("nome"))
                        .descricao(resultSet.getString("descricao"))
                        .tipoExameID(resultSet.getInt("tipo_exame_id"))
                        .periodicidade(resultSet.getInt("periodicidade"))
                        .build();
                particularidadeList.add(particularidade);
            }
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao carregar particularidades do funcionário");
        } finally {
            close(resultSet, preparedStatement);
        }
        return particularidadeList;
    }


    public void deletarParticularidade(int id) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(DELETAR_PARTICULARIDADE);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            commit(connection);
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao deletar particularidade de condicoes");
        } finally {
            close(resultSet, preparedStatement);
        }
    }
}
