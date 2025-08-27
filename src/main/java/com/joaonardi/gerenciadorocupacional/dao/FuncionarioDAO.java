package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.Setor;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FuncionarioDAO {
    DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd") ;
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;

    private static String DRIVER = "org.sqlite.JDBC";
    private static String BD = "jdbc:sqlite:resources/_db/db_gerenciador.db";

    private static String CADASTRAR_FUNCIONARIO = " INSERT INTO FUNCIONARIOS "
            + "(id, nome ,cpf, data_nascimento, data_admissao, setor_id, ativo)"
            + "VALUES (NULL, ?, ?, ?, ?, ?, ?) ";

    private static String CONSULTAR_FUNCIONARIO = " SELECT * FROM FUNCIONARIOS "
            + "WHERE ID = ?";

    private static String ALTERAR_FUNCIONARIO = "UPDATE FUNCIONARIOS SET "
            + "nome = ?,  cpf = ?, data_nascimento = ?, data_admissao = ?, setor_id = ?, ativo = ?"
            + "WHERE id = ?";

    private static String ALTERAR_ATIVO = "UPDATE FUNCIONARIOS SET "
            + "ativo = ?"
            + "WHERE id = ?";
    private static String DELETAR_FUNCIONARIO = "DELETE FROM FUNCIONARIOS "
            + "WHERE id = ? and ativo = false ";


   private static String LISTAR_FUNCIONARIOS_POR_STATUS = "SELECT * FROM FUNCIONARIOS "
            + "WHERE 1=1 AND ativo = ? ";

   public FuncionarioDAO(){

   }

    //passar os avisos para a controller

    public void cadastrarFuncionario(Funcionario funcionario){

       Connection connection = DBConexao.getInstance().abrirConexao();
       String query = CADASTRAR_FUNCIONARIO;
       try {
       preparedStatement = connection.prepareStatement(query);
       int i = 1;
       preparedStatement.setString(i++, funcionario.getNome());
       preparedStatement.setString(i++, funcionario.getCpf());
       preparedStatement.setString(i++, funcionario.getDataNascimento().format(formato));
       preparedStatement.setString(i++, funcionario.getDataAdmissao().format(formato));
       preparedStatement.setInt(i++, funcionario.getSetor());
       preparedStatement.setBoolean(i++, funcionario.getAtivo());

       preparedStatement.execute();
       connection.commit();

           JOptionPane.showMessageDialog(null,"Funcionario cadastrado com sucesso");
       } catch (SQLException e) {
           throw new RuntimeException(e);
       }finally {
           DBConexao.getInstance().fechaConexao(resultSet,preparedStatement);

       }
   }

    public void alterarAtivo(String id,Boolean ativo){
        Connection connection = DBConexao.getInstance().abrirConexao();
        String query = ALTERAR_ATIVO;
        try {
            preparedStatement = connection.prepareStatement(query);
            int i = 1;
            preparedStatement.setBoolean(i++,ativo);
            preparedStatement.setString(i++, id);

            preparedStatement.execute();
            connection.commit();
            String resultado = ativo ? "ativado" : "desativado";

            JOptionPane.showMessageDialog(null,"Funcionario "+resultado+" com sucesso");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DBConexao.getInstance().fechaConexao(resultSet,preparedStatement);

        }
    }

    public void deletarFuncionario(String id){
        Connection connection = DBConexao.getInstance().abrirConexao();
        String query = DELETAR_FUNCIONARIO;
        try {
            preparedStatement = connection.prepareStatement(query);
            int i = 1;
            preparedStatement.setString(i++, id);

            preparedStatement.execute();
            connection.commit();

            JOptionPane.showMessageDialog(null,"Funcionario deletado com sucesso");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DBConexao.getInstance().fechaConexao(resultSet,preparedStatement);

        }
    }

    public Funcionario consultarFuncionario(String id) throws Exception {
        Connection connection = DBConexao.getInstance().abrirConexao();
        Funcionario funcionario = null;
        String query = CONSULTAR_FUNCIONARIO;
        try {
            preparedStatement = connection.prepareStatement(query);
            int i = 1;
            preparedStatement.setString(i++, id);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                        funcionario = Funcionario.FuncionarioBuilder.builder()
                                .id(resultSet.getInt("id"))
                                .nome(resultSet.getString("nome"))
                                .cpf(resultSet.getString("cpf"))
                                .dataNascimento(LocalDate.parse(resultSet.getString("data_nascimento")))
                                .dataAdmissao(LocalDate.parse(resultSet.getString("data_admissao")))
                                .setor(resultSet.getInt("setor_id"))
                                .ativo(resultSet.getBoolean("ativo"))
                                .build();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DBConexao.getInstance().fechaConexao(resultSet,preparedStatement);

        }
        if (funcionario == null) {
            JOptionPane.showMessageDialog(null,"Náo foi possivel localizar funcionario selecionado"
                    ,"",JOptionPane.WARNING_MESSAGE);
            throw new Exception("Náo foi possivel localizar funcionario selecionado");
        }
        return funcionario;
    }

    public void alterarFuncionario(int id, Funcionario funcionario){
        Connection connection = DBConexao.getInstance().abrirConexao();
        String query = ALTERAR_FUNCIONARIO;
        try {
            preparedStatement = connection.prepareStatement(query);
            int i = 1;
            preparedStatement.setString(i++, funcionario.getNome());
            preparedStatement.setString(i++, funcionario.getCpf());
            preparedStatement.setString(i++, funcionario.getDataNascimento().format(formato));
            preparedStatement.setString(i++, funcionario.getDataAdmissao().format(formato));
            preparedStatement.setObject(i++, funcionario.getSetor());
            preparedStatement.setBoolean(i++, funcionario.getAtivo());
            preparedStatement.setInt(i++, id);


            preparedStatement.executeUpdate();
            connection.commit();

            JOptionPane.showMessageDialog(null,"Funcionario alterado com sucesso");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DBConexao.getInstance().fechaConexao(resultSet,preparedStatement);

        }
    }

    public ObservableList<Funcionario> listaFuncionariosPorStatus(Boolean ativo) throws Exception {

        // arrumar isso aqui
        Connection connection = DBConexao.getInstance().abrirConexao();
        Funcionario funcionario = null;
        ObservableList<Funcionario> listaFuncionariosAtivos = FXCollections.observableArrayList();
        String query = LISTAR_FUNCIONARIOS_POR_STATUS;

        try {
            int i = 1;
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setBoolean(i++,ativo);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                funcionario = Funcionario.FuncionarioBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .nome(resultSet.getString("nome"))
                        .cpf(resultSet.getString("cpf"))
                        .dataNascimento(LocalDate.parse(resultSet.getString("data_nascimento")))
                        .dataAdmissao(LocalDate.parse(resultSet.getString("data_admissao")))
                        .setor(resultSet.getInt("setor_id"))
                        .ativo(resultSet.getBoolean("ativo"))
                        .build();
                listaFuncionariosAtivos.add(funcionario);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DBConexao.getInstance().fechaConexao(resultSet,preparedStatement);

        }

        return listaFuncionariosAtivos;
    }

}

