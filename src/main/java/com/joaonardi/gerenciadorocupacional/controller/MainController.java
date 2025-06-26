package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
Janela janela = new Janela();
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


}
