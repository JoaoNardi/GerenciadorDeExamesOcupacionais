package com.joaonardi.gerenciadorocupacional.dao;

import com.joaonardi.gerenciadorocupacional.model.Exame;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.Setor;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import com.joaonardi.gerenciadorocupacional.util.DBConexao;
import com.joaonardi.gerenciadorocupacional.util.FormataData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public class ExameDAO extends BaseDAO {
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    private static final String CADASTRAR_EXAME = "INSERT INTO EXAMES (id, tipo_exame_id, funcionario_id, data_emissao, data_validade, " +
            "atualizado_por)"
            + " VALUES (NULL, ?, ?, ?, ?, ?)";

    private static final String ALTERAR_EXAME = "UPDATE EXAMES SET tipo_exame_id = ?, data_emissao = ?, data_validade = ?, atualizado_por = ? WHERE" +
            " id = ?";
    private static final String LIMPAR_ATUALIZADO_POR = "UPDATE EXAMES SET atualizado_por = NULL WHERE atualizado_por = ? ";
    private static final String DELETAR_EXAME = "DELETE FROM EXAMES WHERE id = ?";
    private static final String LISTAR_EXAMES_VIGENTES = "SELECT e.*, te.id as te_id, te.nome as te_nome, " +
            "f.id as f_id, f.nome as f_nome, f.cpf as f_cpf, f.data_nascimento as f_data_nascimento, " +
            "f.data_admissao as f_data_admissao, f.setor_id as f_setor_id, f.ativo as f_ativo, " +
            "s.id as s_id, s.area as s_area " +
            "FROM EXAMES e " +
            "JOIN funcionarios f on f.id = e.funcionario_id " +
            "JOIN setores s on s.id = f_setor_id " +
            "JOIN tipos_exame te on te.id = e.tipo_exame_id " +
            "WHERE atualizado_por is NULL";


    private static final String LISTAR_TODOS_EXAME = "SELECT * FROM EXAMES";

    public ExameDAO() {
    }

    public Exame cadastrarExame(Exame exame) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(CADASTRAR_EXAME, Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            preparedStatement.setInt(i++, exame.getTipoExame().getId());
            preparedStatement.setInt(i++, exame.getFuncionario().getId());
            preparedStatement.setString(i++, FormataData.iso(exame.getDataEmissao()));
            preparedStatement.setString(i++, FormataData.iso(exame.getDataValidade()));
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
            trataSqlExceptions(e, "Erro ao cadastrar exame");
        } finally {
            close(resultSet, preparedStatement);
        }
        return exame;
    }

    public void alterarExame(int id, Exame exame) {
        Connection connection = DBConexao.getInstance().abrirConexao();
        try {
            preparedStatement = connection.prepareStatement(ALTERAR_EXAME);
            int i = 1;
            preparedStatement.setInt(i++, exame.getTipoExame().getId());
            preparedStatement.setString(i++, FormataData.iso(exame.getDataEmissao()));
            preparedStatement.setString(i++, FormataData.iso(exame.getDataValidade()));
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
            trataSqlExceptions(e, "Erro ao alterar exame");
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
            trataSqlExceptions(e, "Erro ao deletar exame");
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
                TipoExame tipoExame = TipoExame.TipoExameBuilder.builder()
                        .id(resultSet.getInt("te_id"))
                        .nome(resultSet.getString("te_nome"))
                        .build();

                Setor setor = Setor.SetorBuilder.builder()
                        .id(resultSet.getInt("s_id"))
                        .area(resultSet.getString("s_area"))
                        .build();

                Funcionario funcionario = Funcionario.FuncionarioBuilder.builder()
                        .id(resultSet.getInt("f_id"))
                        .nome(resultSet.getString("f_nome"))
                        .cpf(resultSet.getString("f_cpf"))
                        .dataNascimento(LocalDate.parse(resultSet.getString("f_data_nascimento")))
                        .dataAdmissao(LocalDate.parse(resultSet.getString("f_data_admissao")))
                        .setor(setor)
                        .build();

                Integer atualizadoPor = resultSet.getInt("atualizado_por");
                if (resultSet.wasNull()) {
                    atualizadoPor = null;
                }
                Exame exame = Exame.ExameBuilder.builder()
                        .id(resultSet.getInt("id"))
                        .tipoExame(tipoExame)
                        .funcionario(funcionario)
                        .dataEmissao(LocalDate.parse(resultSet.getString("data_emissao")))
                        .dataValidade(resultSet.getString("data_validade") != null ?
                                LocalDate.parse(resultSet.getString("data_validade")) : null)
                        .atualizadoPor(atualizadoPor)
                        .build();
                listaExames.add(exame);
            }
        } catch (SQLException e) {
            rollback(connection);
            trataSqlExceptions(e, "Erro ao carregar exames");
        } finally {
            close(resultSet, preparedStatement);
        }
        return listaExames;
    }
}
