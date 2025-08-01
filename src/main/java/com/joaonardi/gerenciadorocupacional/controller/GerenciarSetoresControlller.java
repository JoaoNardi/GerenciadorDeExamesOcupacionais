package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.dao.SetorDAO;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.GrauDeRisco;
import com.joaonardi.gerenciadorocupacional.model.Setor;
import com.joaonardi.gerenciadorocupacional.service.SetorService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GerenciarSetoresControlller {
    public TableView<Setor> tabelaSetores;
    public TableColumn colunaArea;
    public TableColumn colunaRisco;

    ObservableList<Setor> setores;
    Janela janela = new Janela();
    SetorController setorController = new SetorController();
    SetorService setorService = new SetorService();
    private SetorDAO setorDAO;

    @FXML
    public void initialize() {

        setores = setorService.carregarSetores();

        colunaArea.setCellValueFactory(new PropertyValueFactory<>("area"));
        colunaRisco.setCellValueFactory(new PropertyValueFactory<>("grauRisco"));
        colunaRisco.setCellFactory(c -> new TableCell<Setor, Integer>(){
                    @Override
                    protected void updateItem(Integer grauRisco, boolean empty) {
                        super.updateItem(grauRisco, empty);
                        if (empty || grauRisco == null){
                            setText(null);
                        }else {
                            GrauDeRisco risco = GrauDeRisco.values()[grauRisco];
                            setText(risco.toString());

                        }
                    }
                }
        );
        tabelaSetores.setItems(setores);
    }

    public void handleTableDoubleClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2){
            Setor setorSelecionado = tabelaSetores.getSelectionModel().getSelectedItem();
        janela.abrirJanela("/view/SetorView.fxml","Editar Setor", null);
        setorController = janela.loader.getController();
        setorController.setSetor(setorSelecionado);
            janela.stage.setOnHidden(e -> {setores.clear(); setores.addAll(setorService.carregarSetores());
            tabelaSetores.setItems(setores);
            });
        }
    }
}
