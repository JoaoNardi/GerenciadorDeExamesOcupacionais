package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.dao.SetorDAO;
import com.joaonardi.gerenciadorocupacional.model.Exame;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.Setor;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

public class MainController {
    public TableView<Funcionario> tabelaPrincipal;
    public TableColumn<Funcionario, String> colunaFuncionario;
    public TableColumn<Funcionario, String> colunaIdade;
    public TableColumn<Funcionario, String> colunaSetor;
    public TableColumn<Funcionario, String> colunaAniversario;
    public TableColumn<Funcionario, String> colunaStatusGeral;
    public TableColumn<Funcionario, Button> colunaAcoes;
    public Button btnVencidos;
    public Button btnVencemSemana;
    public Button btnVencemMes;
    public Button btnVencemSemestre;
    public Button btnTodos;
    public Label labelTodos;
    public Label labelVencemMes;
    public Label labelVencemSemana;
    public Label labelVencidos;
    Janela janela = new Janela();

    ObservableList<Funcionario> funcionariosList = FXCollections.observableArrayList();
    FuncionarioService funcionarioService = new FuncionarioService();
    ExameService exameService = new ExameService();
    ObservableList<Exame> listaDeExames = exameService.listarExames();

    @FXML
    private void initialize() throws Exception {
        labelTodos.setText(String.valueOf(funcionariosList.size()));
        recarregarListaFuncionarios();
        SetorCache.carregarSetores();
        colunaFuncionario.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaIdade.setCellValueFactory(funcionarioStringCellDataFeatures -> {
            Funcionario f = funcionarioStringCellDataFeatures.getValue();
            int idade = funcionarioService.calcularIdade(f.getDataNascimento());
            return new ReadOnlyObjectWrapper<>(idade).asString();
        });
        colunaSetor.setCellValueFactory(f -> {
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
}
