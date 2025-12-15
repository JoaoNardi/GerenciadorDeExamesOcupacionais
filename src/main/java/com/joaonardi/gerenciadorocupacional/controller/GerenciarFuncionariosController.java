package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.service.FuncionarioService;
import com.joaonardi.gerenciadorocupacional.service.SetorService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;


public class GerenciarFuncionariosController {
    public TableView<Funcionario> tabelaFuncionarios;
    public TableColumn<Funcionario, String> colunaNome;
    public TableColumn<Funcionario, String> colunaCpf;
    public TableColumn<Funcionario, LocalDate> colunaDataNascimento;
    public TableColumn<Funcionario, String> colunaSetor;
    public TableColumn<Funcionario, String> colunaDataAdmissao;
    public TableColumn<Funcionario, Boolean> colunaAtivo;
    public RadioButton inputAtivo;
    public RadioButton inputInativo;
    public Button inputEditar;

    boolean listarAtivos = true;
    FuncionarioController funcionarioController = new FuncionarioController();
    final FuncionarioService funcionarioService = new FuncionarioService();
    final SetorService setorService = new SetorService();

    @FXML
    private void initialize() {
        inputInativo.setSelected(false);
        inputAtivo.setSelected(true);
        recarregarListaFuncionarios();
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colunaDataNascimento.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
        colunaSetor.setCellValueFactory(f -> {
            return new SimpleStringProperty(f.getValue().getSetor().getArea());
        });
        colunaDataAdmissao.setCellValueFactory(new PropertyValueFactory<>("dataAdmissao"));
        colunaAtivo.setCellValueFactory(new PropertyValueFactory<>("ativo"));


    }

    private void recarregarListaFuncionarios() {
        funcionarioService.carregarFuncionariosPorStatus(listarAtivos);
        tabelaFuncionarios.setItems(funcionarioService.listarFuncionarios());
    }

    @FXML
    public void handleEditar() {
        Funcionario funcionarioSelecionado = tabelaFuncionarios.getSelectionModel().getSelectedItem();
        if (funcionarioSelecionado != null) {
            Janela janelaEditarFuncionario = new Janela();
            janelaEditarFuncionario.abrirJanela("/view/FuncionarioView.fxml", "Editar Funcionario", (Stage) tabelaFuncionarios.getScene().getWindow(), null);
            funcionarioController = janelaEditarFuncionario.loader.getController();
            funcionarioController.setFuncionario(funcionarioSelecionado);
        }
    }

    @FXML
    private void handleTableDoubleClick(javafx.scene.input.MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            handleEditar();
        }
    }

    public void handleListarAtivos() {
        inputAtivo.setSelected(true);
        inputInativo.setSelected(false);
        listarAtivos = true;
        recarregarListaFuncionarios();
    }

    public void handleListarInativos() {
        inputInativo.setSelected(true);
        inputAtivo.setSelected(false);
        listarAtivos = false;
        recarregarListaFuncionarios();
    }


}
