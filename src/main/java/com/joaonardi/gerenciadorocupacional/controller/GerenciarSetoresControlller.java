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
import javafx.stage.Stage;

public class GerenciarSetoresControlller {
    public TableView<Setor> tabelaSetores;
    public TableColumn<Setor, String> colunaArea;

    ObservableList<Setor> setores = FXCollections.observableArrayList();
    SetorController setorController = new SetorController();
    final SetorService setorService = new SetorService();

    @FXML
    public void initialize() {
        setorService.carregarSetores();
        setores = setorService.listarSetores();
        colunaArea.setCellValueFactory(new PropertyValueFactory<>("area"));
        tabelaSetores.setItems(setores);
    }

    public void handleTableDoubleClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            Setor setorSelecionado = tabelaSetores.getSelectionModel().getSelectedItem();
            Janela janelaEditarSetor = new Janela();
            janelaEditarSetor.abrirJanela("/view/SetorView.fxml", "Editar Setor", (Stage) tabelaSetores.getScene().getWindow(), null);
            setorController = janelaEditarSetor.loader.getController();
            setorController.setSetor(setorSelecionado);
            janelaEditarSetor.stage.setOnHidden(e -> {
                setores.clear();
                setores.addAll(setorService.listarSetores());
                tabelaSetores.setItems(setores);
            });
        }
    }
}
