package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.exception.DataNotFoundException;
import com.joaonardi.gerenciadorocupacional.exception.DbException;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FuncionarioDAO extends BaseDAO {
    final DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private static final String CADASTRAR_FUNCIONARIO = " INSERT INTO FUNCIONARIOS "
            + "(id, nome ,cpf, data_nascimento, data_admissao, setor_id, ativo)"
            + "VALUES (NULL, ?, ?, ?, ?, ?, ?) ";
    private static final String CONSULTAR_FUNCIONARIO = " SELECT * FROM FUNCIONARIOS "
            + "WHERE ID = ?";
    private static final String DELETAR_FUNCIONARIO = "DELETE FROM FUNCIONARIOS "
            + "WHERE id = ? and ativo = false ";
    private static final String ALTERAR_FUNCIONARIO = "UPDATE FUNCIONARIOS SET "
            + "nome = ?,  cpf = ?, data_nascimento = ?, data_admissao = ?, setor_id = ?, ativo = ?"
            + "WHERE id = ?";
    private static final String LISTAR_FUNCIONARIOS_POR_STATUS = "SELECT * FROM FUNCIONARIOS "
            + "WHERE 1=1 AND ativo = ? ";
    private static final String LISTAR_TODOS_FUNCIONARIOS = "SELECT * FROM FUNCIONARIOS ";

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
            preparedStatement.setInt(i++, funcionario.getIdSetor());
            preparedStatement.setBoolean(i++, funcionario.getAtivo());

            preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
            throw new DbException("Erro ao cadastrar funcionário", e);
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
            throw new DbException("Erro ao deletar funcionário", e);
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public Funcionario consultarFuncionario(String id) throws Exception {
        Connection connection = DBConexao.getInstance().abrirConexao();
        Funcionario funcionario = null;

        try {
            preparedStatement = connection.prepareStatement(CONSULTAR_FUNCIONARIO);
            int i = 1;
            preparedStatement.setString(i++, id);

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
            }

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new DbException("Erro ao realizar rollback após falha", ex);
            }
            throw new DbException("Erro ao consultar funcionario", e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet, preparedStatement);
        }
        return funcionario;
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
            preparedStatement.setObject(i++, funcionario.getIdSetor());
            preparedStatement.setBoolean(i++, funcionario.getAtivo());
            preparedStatement.setInt(i++, id);


            preparedStatement.executeUpdate();
            connection.commit();

        } catch (SQLException e) {
            rollback(connection);
            throw new DbException("Erro ao alterar funcionário", e);
        } finally {
            close(resultSet, preparedStatement);
        }
    }

    public ObservableList<Funcionario> listaFuncionariosPorStatus(Boolean ativo) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        Funcionario funcionario = null;
        ObservableList<Funcionario> listaFuncionariosAtivos = FXCollections.observableArrayList();
        try {
            preparedStatement = connection.prepareStatement(LISTAR_FUNCIONARIOS_POR_STATUS);
            preparedStatement.setBoolean(1, ativo);
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
                listaFuncionariosAtivos.add(funcionario);
            }
            if (listaFuncionariosAtivos.isEmpty()){
                throw new DataNotFoundException("Funcionarios nao encontrados");
            }
        } catch (SQLException e) {
            rollback(connection);
            throw new DbException("Erro ao carregar funcionários", e);
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
                funcionario = Funcionario.FuncionarioBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .nome(resultSet.getString("nome"))
                        .cpf(resultSet.getString("cpf"))
                        .dataNascimento(LocalDate.parse(resultSet.getString("data_nascimento")))
                        .dataAdmissao(LocalDate.parse(resultSet.getString("data_admissao")))
                        .idSetor(resultSet.getInt("setor_id"))
                        .ativo(resultSet.getBoolean("ativo"))
                        .build();
                listaFuncionariosAtivos.add(funcionario);
            }
            if (listaFuncionariosAtivos.isEmpty()){
                throw new DataNotFoundException("Funcionarios nao encontrados");
            }
        } catch (SQLException e) {
            rollback(connection);
            throw new DbException("Erro ao carregar funcionários (Sem status)", e);
        } finally {
            close(resultSet, preparedStatement);
        }
        return listaFuncionariosAtivos;
    }
}

