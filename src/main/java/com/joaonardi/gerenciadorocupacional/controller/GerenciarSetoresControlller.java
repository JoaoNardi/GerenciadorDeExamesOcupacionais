package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Setor;
import com.joaonardi.gerenciadorocupacional.service.SetorService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


public class GerenciarSetoresControlller {
    public TableView<Setor> tabelaSetores;
    public TableColumn<Setor, String> colunaArea;
    public Button btnEditar;
    public Button btnNovo;

    ObservableList<Setor> setores = FXCollections.observableArrayList();
    SetorController setorController = new SetorController();
    final SetorService setorService = new SetorService();

    @FXML
    public void initialize() {

//        this.editar = btnEditar;
//        this.tabela = tabelaSetores;
//        this.diretorio = "/view/SetorView.fxml";
    }


}
