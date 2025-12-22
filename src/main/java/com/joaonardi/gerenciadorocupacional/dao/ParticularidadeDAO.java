package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.model.*;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public class ParticularidadeDAO extends BaseDAO {
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    //tabela descritiva
    private static final String CADASTRAR_PARTICULARIDADE = "INSERT INTO particularidades (id, nome, descricao, tipo_exame_id, periodicidade) " +
            "VALUES (NULL, ?, ?, ?, ?)";
    private static final String CONSULTAR_PARTICULARIDADE = "SELECT * FROM particularidades WHERE ID = ?";
    private static final String ALTERAR_PARTICULARIDADE = "UPDATE particularidades SET nome = ?, descricao = ?, tipo_exame_id = ?, " +
            "periodicidade = ? WHERE id = ? ";
    private static final String DELETAR_PARTICULARIDADE = "DELETE FROM particularidades WHERE id = ?";
    private static final String LISTAR_TODOS_PARTICULARIDADES = "SELECT p.*, t.id AS t_id, t.nome AS t_nome " +
            "FROM particularidades p " +
            "JOIN tipos_exame t ON t.id = p.tipo_exame_id ";
    private static final String LISTAR_PARTICULARIDADES_TIPO_EXAME_ID = "SELECT p.*, t.id AS t_id, t.nome AS t_nome" +
            "FROM particularidades p " +
            "JOIN tipos_exame t ON t.id = p.tipo_exame_id " +
            "WHERE p.tipo_exame_id = ?";

    //tabela de vinculos

    private static final String VINCULAR_PARTICULARIDADE_FUNCIONARIO = "INSERT INTO vinculos_particularidades (id, funcionario_id, " +
            "particularidade_id, motivo) VALUES (NULL, ?, ?, ?)";
    private static final String ALTERAR_MOTIVO_VINCULO = "UPDATE particularidades SET motivo = ? WHERE particularidade_id = ? AND" +
            "WHERE funcionario_id = ?";
    private static final String LISTAR_FUNCIONARIOS_VINCULADOS_PARTICULARIDADE = "SELECT f.*, s.id as s_id, s.area as s_area " +
            "FROM vinculos_particularidades vp" +
            "JOIN setores s on s.id = f.setor_id" +
            "JOIN funcionarios f ON f.id = vp.funcionario_id" +
            "WHERE vp.particularidade_id = ?;";
    private static final String LISTAR_PARTICULARIDADES_VINCULADOS_FUNCIONARIO = "SELECT p.* , t.id AS t_id, t.nome AS t_nome" +
            "FROM vinculos_particularidades vp" +
            "JOIN particularidade p ON p.id = vp.particularidade_id" +
            "JOIN tipos_exame t on t.id p.tipo_exame_id" +
            "WHERE vp.funcionario_id = ?;";


    private static final String LISTAR_TODOS_VINCULOS = "SELECT vp.id as vp_id, vp.motivo as vp_motivo, " +
            "p.id as p_id, p.nome as p_nome, p.descricao as p_descricao, p.tipo_exame_id as p_tipo_exame_id, p.periodicidade as p_periodicidade, " +
            "f.id as f_id, f.nome as f_nome, f.cpf as f_cpf, f.data_nascimento as f_data_nascimento, " +
            "f.data_admissao as f_data_admissao, f.setor_id as f_setor_id, f.ativo as f_ativo, " +
            "s.id as s_id, s.area as s_area, " +
            "t.id AS t_id, t.nome AS t_nome " +
            "FROM vinculos_particularidades vp " +
            "JOIN particularidades p ON p.id = vp.particularidade_id " +
            "JOIN funcionarios f ON f.id = vp.funcionario_id " +
            "JOIN tipos_exame t on t.id = p.tipo_exame_id " +
            "JOIN setores s on s.id = f_setor_id ";

    private static final String DESVINCULAR_PARTICULARIDADE_FUNCIONARIO = "DELETE FROM vinculos_particularidades WHERE particularidade_id = ? AND " +
            "funcionario_id = ?  ";

    public Particularidade cadastrarParticularidade(Particularidade particularidade) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(CADASTRAR_PARTICULARIDADE, Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            preparedStatement.setString(i++, particularidade.getNome());
            preparedStatement.setString(i++, particularidade.getDescricao());
            preparedStatement.setInt(i++, particularidade.getTipoExame().getId());
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
            trataSqlExceptions(e, "Erro ao cadastrar particularidade");
        } finally {
            close(resultSet, preparedStatement);
        }
        return particularidade;
    }

    public ObservableList<Particularidade> listarTodasParticularidades() {
        Connection connection = DBConexao.getInstance().abrirConexao();
        ObservableList<Particularidade> particularidadeList = FXCollections.observableArrayList();
        try {
            preparedStatement = connection.prepareStatement(LISTAR_TODOS_PARTICULARIDADES);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                TipoExame tipoExameObj = TipoExame.TipoExameBuilder.builder()
                        .id(resultSet.getInt("t_id"))
                        .nome(resultSet.getString("t_nome"))
                        .build();

                Particularidade particularidade = Particularidade.ParticularidadeBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .nome(resultSet.getString("nome"))
                        .descricao(resultSet.getString("descricao"))
                        .tipoExame(tipoExameObj)
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

    public ObservableList<Particularidade> listarParticularidadesPorTipoExameId(TipoExame tipoExame) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        ObservableList<Particularidade> particularidadeList = FXCollections.observableArrayList();
        try {
            preparedStatement = connection.prepareStatement(LISTAR_PARTICULARIDADES_TIPO_EXAME_ID);
            preparedStatement.setInt(1, tipoExame.getId());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                TipoExame tipoExameObj = TipoExame.TipoExameBuilder.builder()
                        .id(resultSet.getInt("t_id"))
                        .nome(resultSet.getString("t_nome"))
                        .build();

                Particularidade particularidade = Particularidade.ParticularidadeBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .nome(resultSet.getString("nome"))
                        .descricao(resultSet.getString("descricao"))
                        .tipoExame(tipoExameObj)
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

    public void vincularParticularidadeFuncionario(Funcionario funcionario, Particularidade particularidade, String motivo) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(VINCULAR_PARTICULARIDADE_FUNCIONARIO);
            int i = 1;
            preparedStatement.setInt(i++, funcionario.getId());
            preparedStatement.setInt(i++, particularidade.getId());
            preparedStatement.setString(i++, motivo);
            preparedStatement.execute();
            commit(connection);
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao vincular particularidade - funcionario");
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public ObservableList<VinculoFuncionarioParticularidade> listarVinculos() {
        Connection connection = DBConexao.getInstance().abrirConexao();
        Funcionario funcionario;
        ObservableList<VinculoFuncionarioParticularidade> list = FXCollections.observableArrayList();
        try {
            preparedStatement = connection.prepareStatement(LISTAR_TODOS_VINCULOS);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                Setor setor = Setor.SetorBuilder.builder()
                        .id(resultSet.getInt("s_id"))
                        .area(resultSet.getString("s_area"))
                        .build();

                funcionario = Funcionario.FuncionarioBuilder.builder()
                        .id(resultSet.getInt("f_id"))
                        .nome(resultSet.getString("f_nome"))
                        .cpf(resultSet.getString("f_cpf"))
                        .dataNascimento(LocalDate.parse(resultSet.getString("f_data_nascimento")))
                        .dataAdmissao(LocalDate.parse(resultSet.getString("f_data_admissao")))
                        .setor(setor)
                        .ativo(resultSet.getBoolean("f_ativo"))
                        .build();

                TipoExame tipoExameObj = TipoExame.TipoExameBuilder.builder()
                        .id(resultSet.getInt("t_id"))
                        .nome(resultSet.getString("t_nome"))
                        .build();

                Particularidade particularidade = Particularidade.ParticularidadeBuilder.builder()
                        .id(resultSet.getInt("p_id"))
                        .nome(resultSet.getString("p_nome"))
                        .descricao(resultSet.getString("p_descricao"))
                        .tipoExame(tipoExameObj)
                        .periodicidade(resultSet.getInt("p_periodicidade"))
                        .build();

                VinculoFuncionarioParticularidade vinculoFuncionarioParticularidade = VinculoFuncionarioParticularidade
                        .VinculoFuncionarioParticularidadeBuilder.builder()
                        .id(resultSet.getInt("vp_id"))
                        .funcionario(funcionario)
                        .particularidade(particularidade)
                        .motivo(resultSet.getString("vp_motivo"))
                        .build();
                list.add(vinculoFuncionarioParticularidade);

            }
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao carregar funcionario vinculados a particularidade");
        } finally {
            close(resultSet, preparedStatement);
        }
        return list;
    }

    public void atualizarMotivoVinculo(Particularidade particularidade, Funcionario funcionario, String motivo) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(ALTERAR_MOTIVO_VINCULO);
            int i = 1;
            preparedStatement.setString(i++, motivo);
            preparedStatement.setInt(i++, particularidade.getId());
            preparedStatement.setInt(i++, funcionario.getId());

            int linhasAfetadas = preparedStatement.executeUpdate();
            commit(connection);

            if (linhasAfetadas == 0) {
                connection.rollback();
            }
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao alterar vinculo");
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
                Setor setor = Setor.SetorBuilder.builder()
                        .id(resultSet.getInt("s_id"))
                        .area(resultSet.getString("s_area"))
                        .build();

                funcionario = Funcionario.FuncionarioBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .nome(resultSet.getString("nome"))
                        .cpf(resultSet.getString("cpf"))
                        .dataNascimento(LocalDate.parse(resultSet.getString("data_nascimento")))
                        .dataAdmissao(LocalDate.parse(resultSet.getString("data_admissao")))
                        .setor(setor)
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

        ObservableList<Particularidade> particularidadeList = FXCollections.observableArrayList();
        try {
            preparedStatement = connection.prepareStatement(LISTAR_PARTICULARIDADES_VINCULADOS_FUNCIONARIO);
            preparedStatement.setInt(1, funcionario.getId());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                TipoExame tipoExameObj = TipoExame.TipoExameBuilder.builder()
                        .id(resultSet.getInt("t_id"))
                        .nome(resultSet.getString("t_nome"))
                        .build();

                Particularidade particularidade = Particularidade.ParticularidadeBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .nome(resultSet.getString("nome"))
                        .descricao(resultSet.getString("descricao"))
                        .tipoExame(tipoExameObj)
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


    public void deletarParticularidade(Particularidade particularidade) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(DELETAR_PARTICULARIDADE);
            preparedStatement.setInt(1, particularidade.getId());
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
