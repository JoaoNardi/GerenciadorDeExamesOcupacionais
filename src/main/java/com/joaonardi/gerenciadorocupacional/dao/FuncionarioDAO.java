package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.Setor;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FuncionarioDAO extends BaseDAO {
    final DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private static final String CADASTRAR_FUNCIONARIO = " INSERT INTO FUNCIONARIOS "
            + "(id, nome ,cpf, data_nascimento, data_admissao, setor_id, ativo)"
            + "VALUES (NULL, ?, ?, ?, ?, ?, ?) ";
    private static final String DELETAR_FUNCIONARIO = "DELETE FROM FUNCIONARIOS "
            + "WHERE id = ? and ativo = false ";
    private static final String ALTERAR_FUNCIONARIO = "UPDATE FUNCIONARIOS SET "
            + "nome = ?,  cpf = ?, data_nascimento = ?, data_admissao = ?, setor_id = ?, ativo = ?"
            + "WHERE id = ?";
    private static final String ATIVAR_INATIVAR_FUNCIONARIO = "UPDATE FUNCIONARIOS SET "
            + "ativo = ?"
            + "WHERE id = ?";

    private static final String LISTAR_FUNCIONARIOS_POR_STATUS = "SELECT f.*, s.id as s_id, s.area as s_area FROM FUNCIONARIOS f " +
            "JOIN setores s on s.id = f.setor_id " +
            "WHERE 1=1 AND ativo = ? ";
    private static final String LISTAR_TODOS_FUNCIONARIOS = "SELECT f.*, s.id as s_id, s.area as s_area FROM FUNCIONARIOS f " +
            "JOIN setores s on s.id = f.setor_id";

    public FuncionarioDAO() {
    }

    public void cadastrarFuncionario(Funcionario funcionario) {

        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(CADASTRAR_FUNCIONARIO);
            int i = 1;
            preparedStatement.setString(i++, funcionario.getNome());
            preparedStatement.setString(i++, funcionario.getCpf());
            preparedStatement.setString(i++, funcionario.getDataNascimento().format(formato));
            preparedStatement.setString(i++, funcionario.getDataAdmissao().format(formato));
            preparedStatement.setInt(i++, funcionario.getSetor().getId());
            preparedStatement.setBoolean(i++, funcionario.getAtivo());

            preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao cadastrar funcionário");
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public void deletarFuncionario(String id) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(DELETAR_FUNCIONARIO);
            int i = 1;
            preparedStatement.setString(i++, id);

            preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao deletar funcionario");
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public void alterarFuncionario(int id, Funcionario funcionario) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(ALTERAR_FUNCIONARIO);
            int i = 1;
            preparedStatement.setString(i++, funcionario.getNome());
            preparedStatement.setString(i++, funcionario.getCpf());
            preparedStatement.setString(i++, funcionario.getDataNascimento().format(formato));
            preparedStatement.setString(i++, funcionario.getDataAdmissao().format(formato));
            preparedStatement.setObject(i++, funcionario.getSetor().getId());
            preparedStatement.setBoolean(i++, funcionario.getAtivo());
            preparedStatement.setInt(i++, id);

            preparedStatement.executeUpdate();
            connection.commit();

        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao alterar funcionario");
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public ObservableList<Funcionario> listaFuncionariosPorStatus(Boolean ativo) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        ObservableList<Funcionario> listaFuncionariosAtivos = FXCollections.observableArrayList();
        try {
            preparedStatement = connection.prepareStatement(LISTAR_FUNCIONARIOS_POR_STATUS);
            preparedStatement.setBoolean(1, ativo);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Setor setor = Setor.SetorBuilder.builder()
                        .id(resultSet.getInt("s_id"))
                        .area(resultSet.getString("s_area"))
                        .build();

                Funcionario funcionario = Funcionario.FuncionarioBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .nome(resultSet.getString("nome"))
                        .cpf(resultSet.getString("cpf"))
                        .dataNascimento(LocalDate.parse(resultSet.getString("data_nascimento")))
                        .dataAdmissao(LocalDate.parse(resultSet.getString("data_admissao")))
                        .setor(setor)
                        .ativo(resultSet.getBoolean("ativo"))
                        .build();
                listaFuncionariosAtivos.add(funcionario);
            }
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao carregar funcionario");
        } finally {
            close(resultSet, preparedStatement);
        }
        return listaFuncionariosAtivos;
    }

    public ObservableList<Funcionario> listaFuncionariosPorStatus() {
        Connection connection = DBConexao.getInstance().abrirConexao();
        Funcionario funcionario;
        ObservableList<Funcionario> listaFuncionariosAtivos = FXCollections.observableArrayList();
        try {
            preparedStatement = connection.prepareStatement(LISTAR_TODOS_FUNCIONARIOS);
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
                listaFuncionariosAtivos.add(funcionario);
            }
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao carregar funcionários (Sem status)");
        } finally {
            close(resultSet, preparedStatement);
        }
        return listaFuncionariosAtivos;
    }

    public void ativarInativarFuncionario(Funcionario funcionario) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(ATIVAR_INATIVAR_FUNCIONARIO);
            preparedStatement.setBoolean(1, !funcionario.getAtivo());
            preparedStatement.setInt(2, funcionario.getId());

            preparedStatement.executeUpdate();
            connection.commit();

        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao Ativar/Inativar funcionario");
        } finally {
            close(resultSet, preparedStatement);
        }
    }
}

