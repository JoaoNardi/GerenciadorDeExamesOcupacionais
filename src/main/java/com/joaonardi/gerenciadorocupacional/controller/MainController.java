package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Exame;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.service.ExameService;
import com.joaonardi.gerenciadorocupacional.service.FuncionarioService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import com.joaonardi.gerenciadorocupacional.cache.SetorCache;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    public TableColumn<Exame, Integer> colunaIdadeVencimentos;
    public TableColumn<Exame, String> colunaSetorVencimentos;
    public TableColumn<Exame, String> colunaStatusVencimentos;
    public TableColumn<Exame, Node> colunaAcoesVencimentos;

    Janela janela = new Janela();

    ObservableList<Funcionario> funcionariosList = FXCollections.observableArrayList();
    FuncionarioService funcionarioService = new FuncionarioService();
    ExameService exameService = new ExameService();
    ObservableList<Exame> listaDeExames = exameService.listarExames();
    int diasVencimento = 0;

    @FXML
    private void initialize() throws Exception {
        recarregarListaFuncionarios();
        SetorCache.carregarSetores();
        setTabelaPrincipal();
        setTabelaSecundaria();
        labelTodos.setText(String.valueOf(funcionariosList.size()));
        labelVencidos.setText(String.valueOf(exameService.listarExamePorVencimento(0).size()));
        labelVencemSemana.setText(String.valueOf(exameService.listarExamePorVencimento(7).size()));
        labelVencemMes.setText(String.valueOf(exameService.listarExamePorVencimento(30).size()));
        labelVencemSemestre.setText(String.valueOf(exameService.listarExamePorVencimento(182).size()));
    }

    private void setTabelaPrincipal(){
        if (!funcionariosList.isEmpty()) {
            colunaFuncionarioGeral.setCellValueFactory(new PropertyValueFactory<>("nome"));
            colunaIdadeGeral.setCellValueFactory(funcionarioStringCellDataFeatures -> {
                Funcionario f = funcionarioStringCellDataFeatures.getValue();
                int idade = funcionarioService.calcularIdade(f.getDataNascimento());
                return new ReadOnlyObjectWrapper<>(idade).asString();
            });
            colunaSetorGeral.setCellValueFactory(f -> {
                String areaSetor = SetorCache.getSetorMapeado(f.getValue().getSetor());
                return new SimpleStringProperty(areaSetor);
            });
            colunaAniversario.setCellValueFactory(funcionarioStringCellDataFeatures -> {
                Funcionario f = funcionarioStringCellDataFeatures.getValue();
                String dataAniversario = f.getDataNascimento().format(DateTimeFormatter.ofPattern("dd/MM"));
                return new ReadOnlyObjectWrapper<>(dataAniversario);
            });
            tabelaPrincipal.setItems(funcionariosList);
        }
    }

    private void setTabelaSecundaria(){
        colunaFuncionarioVencimentos.setCellValueFactory(new PropertyValueFactory<>("nome"));
        tabelaVencimentos.setItems(listaDeExames);

    }

    private void recarregarListaFuncionarios(){
        try {
            funcionariosList = funcionarioService.carregarFuncionarios(true);
            tabelaPrincipal.setItems(funcionariosList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleAbrirFuncionario(ActionEvent event) {
        janela.abrirJanela("/view/FuncionarioView.fxml","Cadastro de Funcionários");
    }
    @FXML
    public void handleAbrirGerenciarFuncionario(ActionEvent event) {
        janela.abrirJanela("/view/GerenciarFuncionariosView.fxml","Gerenciar Funcionarios");
    }

    @FXML
    public void handleAbrirSetor(ActionEvent event) {
        janela.abrirJanela("/view/SetorView.fxml","Cadastro de Setores");
    }
    @FXML
    public void handleAbrirGerenciarSetor(ActionEvent event) {
        janela.abrirJanela("/view/GerenciarSetoresView.fxml","Gerenciar Setores");
    }
    @FXML
    public void handleAbrirExame(ActionEvent event){
        janela.abrirJanela("/view/TipoExameView.fxml","Cadastro de Exame");
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
        diasVencimento = 0;
    }

    public void handleBtnSemana(ActionEvent event) {
        diasVencimento = 7;
    }

    public void handleBtnMes(ActionEvent event) {
        diasVencimento = 30;
    }

    public void handleBtnSemestre(ActionEvent event) {
        diasVencimento = 182;
    }

    public void handleBtnTodos(ActionEvent event) {
    }
}
