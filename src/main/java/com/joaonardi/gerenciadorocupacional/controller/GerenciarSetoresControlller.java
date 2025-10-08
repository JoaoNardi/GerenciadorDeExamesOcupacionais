package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Setor;
import com.joaonardi.gerenciadorocupacional.service.SetorService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class GerenciarSetoresControlller {
    public TableView<Setor> tabelaSetores;
    public TableColumn<Setor, String> colunaArea;

    ObservableList<Setor> setores = FXCollections.observableArrayList();
    Janela janela = new Janela();
    SetorController setorController = new SetorController();
    SetorService setorService = new SetorService();

    @FXML
    public void initialize() {
        setorService.carregarSetores();
        setores = setorService.listarSetores();
        colunaArea.setCellValueFactory(new PropertyValueFactory<>("area"));
        tabelaSetores.setItems(setores);
    }

    public void handleTableDoubleClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2){
            Setor setorSelecionado = tabelaSetores.getSelectionModel().getSelectedItem();
        janela.abrirJanela("/view/SetorView.fxml","Editar Setor", null);
        setorController = janela.loader.getController();
        setorController.setSetor(setorSelecionado);
            janela.stage.setOnHidden(e -> {setores.clear(); setores.addAll(setorService.listarSetores());
            tabelaSetores.setItems(setores);
            });
        }
    }
}
