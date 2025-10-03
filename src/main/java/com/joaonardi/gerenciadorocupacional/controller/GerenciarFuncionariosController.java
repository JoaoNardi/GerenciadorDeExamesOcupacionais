package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.service.FuncionarioService;
import com.joaonardi.gerenciadorocupacional.service.SetorService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;


public class GerenciarFuncionariosController {
    public TableView<Funcionario> tabelaFuncionarios;
    public TableColumn<Funcionario, String> colunaNome;
    public TableColumn<Funcionario, String> colunaCpf;
    public TableColumn<Funcionario, String> colunaDataNascimento;
    public TableColumn<Funcionario, String> colunaSetor;
    public TableColumn<Funcionario, String> colunaDataAdmissao;
    public TableColumn<Funcionario, Boolean> colunaAtivo;
    public RadioButton inputAtivo;
    public RadioButton inputInativo;
    public Button inputEditar;

    boolean listarAtivos = true;
    ToggleGroup toggleAtivo = new ToggleGroup();
    FuncionarioController funcionarioController = new FuncionarioController();
    Janela janela = new Janela();
    FuncionarioService funcionarioService = new FuncionarioService();
    SetorService setorService = new SetorService();

    @FXML
    private void initialize() throws Exception {
        inputInativo.setSelected(false);
        inputAtivo.setSelected(true);
        recarregarListaFuncionarios();
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colunaDataNascimento.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
        colunaSetor.setCellValueFactory(f -> {
            String areaSetor = setorService.getSetorMapeado(f.getValue().getIdSetor());
            return new SimpleStringProperty(areaSetor);
        });
        colunaDataAdmissao.setCellValueFactory(new PropertyValueFactory<>("dataAdmissao"));
        colunaAtivo.setCellValueFactory(new PropertyValueFactory<>("ativo"));


    }

    private void recarregarListaFuncionarios() {
        funcionarioService.carregarFuncionariosPorStatus(listarAtivos);
        tabelaFuncionarios.setItems(funcionarioService.listarFuncionarios());
    }

    @FXML
    public void handleEditar() throws Exception {
        Funcionario funcionarioSelecionado = tabelaFuncionarios.getSelectionModel().getSelectedItem();
        if (funcionarioSelecionado != null) {
            janela.abrirJanela("/view/FuncionarioView.fxml", "Editar funcionario", null);
            funcionarioController = janela.loader.getController();
            funcionarioController.setFuncionario(funcionarioSelecionado);
        }
    }

    @FXML
    private void handleTableDoubleClick(javafx.scene.input.MouseEvent mouseEvent) throws Exception {
        if (mouseEvent.getClickCount() == 2) {
            handleEditar();
        }
    }

    public void handleListarAtivos(ActionEvent event) {
        inputAtivo.setSelected(true);
        inputInativo.setSelected(false);
        listarAtivos = true;
        recarregarListaFuncionarios();
    }

    public void handleListarInativos(ActionEvent event) {
        inputInativo.setSelected(true);
        inputAtivo.setSelected(false);
        listarAtivos = false;
        recarregarListaFuncionarios();
    }


}
