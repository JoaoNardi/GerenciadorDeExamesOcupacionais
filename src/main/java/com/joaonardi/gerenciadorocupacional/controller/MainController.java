package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.dao.FuncionarioDAO;
import com.joaonardi.gerenciadorocupacional.dao.SetorDAO;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.Setor;
import com.joaonardi.gerenciadorocupacional.service.FuncionarioService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
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
    Janela janela = new Janela();

    ObservableList<Funcionario> funcionariosList = FXCollections.observableArrayList();
    FuncionarioService funcionarioService = new FuncionarioService();
    SetorDAO setorDAO = new SetorDAO();
    Map<Integer, String> setoresMap = setorDAO.listarSetores().stream()
            .collect(Collectors.toMap(Setor::getId, Setor::getArea));

    @FXML
    private void initialize() throws Exception {
        recarregarListaFuncionarios();
        colunaFuncionario.setCellValueFactory(new  PropertyValueFactory<>("nome"));
        colunaIdade.setCellValueFactory(f->{
           Integer idade = funcionarioService.calcularIdade(f.getValue().getDataNascimento());
            return new SimpleStringProperty(idade.toString());
        });
        colunaSetor.setCellValueFactory(f->{
            String areaSetor = setoresMap.getOrDefault(f.getValue().getSetor(),"Sem Setor");
            return new SimpleStringProperty(areaSetor);
        });
        colunaAniversario.setCellValueFactory(f-> {
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM");
            String aniversario = f.getValue().getDataNascimento().format(formato);
            return new SimpleStringProperty(aniversario);
        });



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
        janela.abrirJanela("/view/ExameView.fxml","Cadastro de Exame");
    }
    @FXML
    public void handleAbrirGerenciarExame(ActionEvent event) {
        janela.abrirJanela("/view/GerenciarExamesView.fxml", "Gerenciar Exames");
    }

    public void handleAbrirCertificado(ActionEvent event) {
        janela.abrirJanela("/view/CertificadoView.fxml", "Cadastro Certificado");
    }

    public void handleAbrirGerenciarCertificado(ActionEvent event) {
        janela.abrirJanela("/view/GerenciarCertificadosView.fxml", "Gerenciar Certificados");
    }

    public void handleLancarExame(ActionEvent event) {
    }

    public void handleGerenciarExames(ActionEvent event) {
    }
}
