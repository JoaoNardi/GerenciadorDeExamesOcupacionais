package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.dao.SetorDAO;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.Setor;
import com.joaonardi.gerenciadorocupacional.service.FuncionarioService;
import com.joaonardi.gerenciadorocupacional.service.SetorService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class GerenciarFuncionariosController {
    public TableView<Funcionario> tabelaFuncionarios;
    public TableColumn<Funcionario, String> colunaNome;
    public TableColumn<Funcionario, String> colunaCpf;
    public TableColumn<Funcionario, String> colunaDataNascimento;
    public TableColumn<Funcionario, String> colunaSetor;
    public TableColumn<Funcionario, String> colunaDataAdmissao;
    public TableColumn<Funcionario, Boolean> colunaAtivo;

    FuncionarioController funcionarioController = new FuncionarioController();
    Janela janela = new Janela();
    FuncionarioService funcionarioService = new FuncionarioService();
    SetorDAO setorDAO = new SetorDAO();
    Map<Integer, String> setoresMap = setorDAO.listarSetores().stream()
            .collect(Collectors.toMap(Setor::getId, Setor::getArea));

    @FXML
    private void initialize() throws Exception {

        ArrayList<Funcionario> funcionariosList = funcionarioService.carregarFuncionarios();
        ObservableList<Funcionario> funcionarios = FXCollections.observableArrayList(funcionariosList);
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colunaDataNascimento.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
        colunaSetor.setCellValueFactory(f->{
            String areaSetor = setoresMap.getOrDefault(f.getValue().getSetor(),"Sem Setor");
        return new SimpleStringProperty(areaSetor);
        });
        colunaDataAdmissao.setCellValueFactory(new PropertyValueFactory<>("dataAdmissao"));
        colunaAtivo.setCellValueFactory(new PropertyValueFactory<>("ativo"));
        tabelaFuncionarios.setItems(funcionarios);

    }
    @FXML
    private void handleTableDoubleClick(javafx.scene.input.MouseEvent mouseEvent) throws Exception {
        if (mouseEvent.getClickCount() == 2){
            Funcionario funcionarioSelecionado = tabelaFuncionarios.getSelectionModel().getSelectedItem();
            if (funcionarioSelecionado != null){

                janela.abrirJanela("/view/FuncionarioView.fxml", "Editar funcionario");
                funcionarioController = janela.loader.getController();
                funcionarioController.setFuncionario(funcionarioSelecionado);


            }
        }

    }
}
