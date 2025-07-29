package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.cache.FuncionarioCache;
import com.joaonardi.gerenciadorocupacional.model.Exame;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.service.*;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;

public class MainController {
    //tabela Principal Geral
    public TableView<Funcionario> tabelaPrincipal;
    public TableColumn<Funcionario, String> colunaFuncionarioGeral;
    public TableColumn<Funcionario, String> colunaIdadeGeral;
    public TableColumn<Funcionario, String> colunaSetorGeral;
    public TableColumn<Funcionario, String> colunaAniversario;
    public TableColumn<Funcionario, String> colunaStatusGeral;
    public TableColumn<Funcionario, Button> colunaAcoesGeral;
    //botoes
    public Button btnVencidos;
    public Button btnVencemSemana;
    public Button btnVencemMes;
    public Button btnVencemSemestre;
    public Button btnTodos;
    public Label labelTodos;
    public Label labelVencemMes;
    public Label labelVencemSemana;
    public Label labelVencemSemestre;
    public Label labelVencidos;
    //tabela Secundaria
    public TableView<Exame> tabelaVencimentos;
    public TableColumn<Exame, String> colunaFuncionarioVencimentos;
    public TableColumn<Exame, String> colunaIdadeVencimentos;
    public TableColumn<Exame, String> colunaSetorVencimentos;
    public TableColumn<Exame, String> colunaTipoVencimentos;
    public TableColumn<Exame, String> colunaDescricaoVencimentos;
    public TableColumn<Exame, String> colunaEmissaoVencimentos;
    public TableColumn<Exame, String> colunaValidadeVencimentos;
    public TableColumn<Exame, String> colunaStatusVencimentos;
    public TableColumn<Exame, Node> colunaAcoesVencimentos;


    Janela janela = new Janela();

    FuncionarioService funcionarioService = new FuncionarioService();
    ExameService exameService = new ExameService();
    MainService mainService = new MainService();
    SetorService setorService = new SetorService();
    TipoExameService tipoExameService = new TipoExameService();
    int diasVencimento = 0;

    @FXML
    private void initialize() throws Exception {
        tabelaVencimentos.setVisible(false);
        mainService.loadInicial();
        setTabelaPrincipal();
        setTabelaSecundaria();
        labelTodos.setText(String.valueOf(exameService.listarExamePorVencimento(183).size()));
        labelVencidos.setText(String.valueOf(exameService.listarExamePorVencimento(0).size()));
        labelVencemSemana.setText(String.valueOf(exameService.listarExamePorVencimento(7).size()));
        labelVencemMes.setText(String.valueOf(exameService.listarExamePorVencimento(30).size()));
        labelVencemSemestre.setText(String.valueOf(exameService.listarExamePorVencimento(182).size()));
    }

    private void setTabelaPrincipal() throws Exception {
        if (!funcionarioService.listarFuncionarios(true).isEmpty()) {
            colunaFuncionarioGeral.setCellValueFactory(new PropertyValueFactory<>("nome"));
            colunaIdadeGeral.setCellValueFactory(funcionarioStringCellDataFeatures -> {
                Funcionario f = funcionarioStringCellDataFeatures.getValue();
                int idade = funcionarioService.calcularIdade(f.getDataNascimento());
                return new ReadOnlyObjectWrapper<>(idade).asString();
            });
            colunaSetorGeral.setCellValueFactory(f -> {
                String areaSetor = setorService.getSetorMapeadoPorId(f.getValue().getSetor());
                return new SimpleStringProperty(areaSetor);
            });
            colunaAniversario.setCellValueFactory(funcionarioStringCellDataFeatures -> {
                Funcionario f = funcionarioStringCellDataFeatures.getValue();
                String dataAniversario = f.getDataNascimento().format(DateTimeFormatter.ofPattern("dd/MM"));
                return new SimpleStringProperty(dataAniversario);
            });
            // TODO: add status geral do Funcionario (Tudo certo, Vencimento proximo etc)
            // TODO: add acoes
            tabelaPrincipal.setItems(funcionarioService.listarFuncionarios(true));
        }
    }

    private void setTabelaSecundaria() {
        colunaFuncionarioVencimentos.setCellValueFactory(f -> {
            String nomeFuncionario = String.valueOf(funcionarioService.getFuncionarioMapeadoPorId(f.getValue().getIdFuncionario()).getNome());
            return new SimpleStringProperty(nomeFuncionario);
        });
        colunaIdadeVencimentos.setCellValueFactory(f -> {
            String idadeFuncionario =
                    String.valueOf(funcionarioService.calcularIdade(funcionarioService.getFuncionarioMapeadoPorId(f.getValue().getIdFuncionario()).getDataNascimento()));
            return new SimpleStringProperty(idadeFuncionario);
        });
        colunaSetorVencimentos.setCellValueFactory(f -> {
            int setorId = funcionarioService.getFuncionarioMapeadoPorId(f.getValue().getIdFuncionario()).getSetor();
            String setorNome = String.valueOf(setorService.getSetorMapeadoPorId(setorId));
            return new SimpleStringProperty(setorNome);
        });
        colunaTipoVencimentos.setCellValueFactory(f -> {
            String tipo = f.getValue().getClass().getSimpleName();
            return new SimpleStringProperty(tipo);
        });
        colunaDescricaoVencimentos.setCellValueFactory(f -> {
            String descricao = tipoExameService.getTipoExameMapeadoPorId(f.getValue().getIdTipoExame()).getNome();
            return new SimpleStringProperty(descricao);
        });
        colunaEmissaoVencimentos.setCellValueFactory(f -> {
            String emissao = f.getValue().getDataEmissao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return new SimpleStringProperty(emissao);
        });
        colunaValidadeVencimentos.setCellValueFactory(f -> {
            String validade = f.getValue().getDataValidade().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return new SimpleStringProperty(validade);
        });
        colunaStatusVencimentos.setCellValueFactory(f -> {
           return new SimpleStringProperty(exameService.vencimentos(f));
        });
        // TODO: add acoes
        tabelaVencimentos.setItems(exameService.listarExames());

    }


    @FXML
    private void handleAbrirFuncionario(ActionEvent event) {
        janela.abrirJanela("/view/FuncionarioView.fxml", "Cadastro de Funcionários");
    }

    @FXML
    public void handleAbrirGerenciarFuncionario(ActionEvent event) {
        janela.abrirJanela("/view/GerenciarFuncionariosView.fxml", "Gerenciar Funcionarios");
    }

    @FXML
    public void handleAbrirSetor(ActionEvent event) {
        janela.abrirJanela("/view/SetorView.fxml", "Cadastro de Setores");
    }

    @FXML
    public void handleAbrirGerenciarSetor(ActionEvent event) {
        janela.abrirJanela("/view/GerenciarSetoresView.fxml", "Gerenciar Setores");
    }

    @FXML
    public void handleAbrirExame(ActionEvent event) {
        janela.abrirJanela("/view/TipoExameView.fxml", "Cadastro de Exame");
    }

    @FXML
    public void handleAbrirGerenciarExame(ActionEvent event) {
        janela.abrirJanela("/view/GerenciarExamesView.fxml", "Gerenciar Exames");
    }

    public void handleAbrirCertificado(ActionEvent event) {
        janela.abrirJanela("/view/TipoCertificadoView.fxml", "Cadastro Certificado");
    }

    public void handleAbrirGerenciarCertificado(ActionEvent event) {
        janela.abrirJanela("/view/GerenciarCertificadosView.fxml", "Gerenciar Certificados");
    }

    public void handleLancarExame(ActionEvent event) {
        janela.abrirJanela("/view/ExamesView.fxml", "Lançar Exames");
    }

    public void handleGerenciarExames(ActionEvent event) {
    }

    public void handleBtnVencidos(ActionEvent event) {
        tabelaPrincipal.setVisible(false);
        tabelaVencimentos.setVisible(true);
        diasVencimento = 0;
        tabelaVencimentos.setItems(exameService.listarExamePorVencimento(diasVencimento));
    }

    public void handleBtnSemana(ActionEvent event) {
        tabelaPrincipal.setVisible(false);
        tabelaVencimentos.setVisible(true);
        diasVencimento = 7;
        tabelaVencimentos.setItems(exameService.listarExamePorVencimento(diasVencimento));
    }

    public void handleBtnMes(ActionEvent event) {
        tabelaPrincipal.setVisible(false);
        tabelaVencimentos.setVisible(true);
        diasVencimento = 30;
        tabelaVencimentos.setItems(exameService.listarExamePorVencimento(diasVencimento));
    }

    public void handleBtnSemestre(ActionEvent event) {
        tabelaPrincipal.setVisible(false);
        tabelaVencimentos.setVisible(true);
        diasVencimento = 182;
        tabelaVencimentos.setItems(exameService.listarExamePorVencimento(diasVencimento));
    }

    public void handleBtnTodos(ActionEvent event) {
        tabelaVencimentos.setVisible(true);
        tabelaPrincipal.setVisible(false);
        tabelaVencimentos.setItems(exameService.listarExames());
    }

    public void handleBtnGeral(ActionEvent event) throws Exception {
        tabelaPrincipal.setVisible(true);
        tabelaVencimentos.setVisible(false);
        tabelaPrincipal.setItems(funcionarioService.listarFuncionarios(true));
    }
}
