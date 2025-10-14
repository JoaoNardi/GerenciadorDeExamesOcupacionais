package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FuncionarioDAO {
    final DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;
    private static final String CADASTRAR_FUNCIONARIO = " INSERT INTO FUNCIONARIOS "
            + "(id, nome ,cpf, data_nascimento, data_admissao, setor_id, ativo)"
            + "VALUES (NULL, ?, ?, ?, ?, ?, ?) ";
    private static final String CONSULTAR_FUNCIONARIO = " SELECT * FROM FUNCIONARIOS "
            + "WHERE ID = ?";
    private static final String ALTERAR_ATIVO = "UPDATE FUNCIONARIOS SET "
            + "ativo = ?"
            + "WHERE id = ?";
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

    //passar os avisos para a controller

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

            JOptionPane.showMessageDialog(null, "Funcionario cadastrado com sucesso");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet, preparedStatement);

        }
    }

    public void alterarAtivo(String id, Boolean ativo) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(ALTERAR_ATIVO);
            int i = 1;
            preparedStatement.setBoolean(i++, ativo);
            preparedStatement.setString(i++, id);

            preparedStatement.execute();
            connection.commit();
            String resultado = ativo ? "ativado" : "desativado";

            JOptionPane.showMessageDialog(null, "Funcionario " + resultado + " com sucesso");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet, preparedStatement);

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

            JOptionPane.showMessageDialog(null, "Funcionario deletado com sucesso");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet, preparedStatement);

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
            throw new RuntimeException(e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet, preparedStatement);

        }
        if (funcionario == null) {
            JOptionPane.showMessageDialog(null, "Náo foi possivel localizar funcionario selecionado"
                    , "", JOptionPane.WARNING_MESSAGE);
            throw new Exception("Náo foi possivel localizar funcionario selecionado");
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

            JOptionPane.showMessageDialog(null, "Funcionario alterado com sucesso");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet, preparedStatement);

        }
    }

    public ObservableList<Funcionario> listaFuncionariosPorStatus(Boolean ativo) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        Funcionario funcionario = null;
        ObservableList<Funcionario> listaFuncionariosAtivos = FXCollections.observableArrayList();
        try {
            int i = 1;
            preparedStatement = connection.prepareStatement(LISTAR_FUNCIONARIOS_POR_STATUS);
            preparedStatement.setBoolean(i++, ativo);
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet, preparedStatement);

        }
        return listaFuncionariosAtivos;
    }

    public ObservableList<Funcionario> listaFuncionariosPorStatus() {
        Connection connection = DBConexao.getInstance().abrirConexao();
        Funcionario funcionario;
        ObservableList<Funcionario> listaFuncionariosAtivos = FXCollections.observableArrayList();
        String query = LISTAR_TODOS_FUNCIONARIOS;

        try {
            int i = 1;
            preparedStatement = connection.prepareStatement(query);
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

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConexao.getInstance().fechaConexao(resultSet, preparedStatement);

        }

        return listaFuncionariosAtivos;
    }

}

