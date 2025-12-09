package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Periodicidade;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import com.joaonardi.gerenciadorocupacional.service.TipoExameService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.swing.*;
import java.sql.SQLException;

public class GerenciarExamesController {
    public TableView<TipoExame> tabelaExames;
    public TableColumn<TipoExame, String> colunaNome;
    public TableColumn<TipoExame, Integer> colunaPerioicidade;
    public Button inputEditar;
    public Button inputNovo;
    public Button inputDeletar;

    ObservableList<TipoExame> tiposExame;
    TipoExameController tipoExameController = new TipoExameController();
    final TipoExameService tipoExameService = new TipoExameService();

    @FXML
    public void initialize() {
        inputEditar.setDisable(true);
        tabelaExames.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
            inputEditar.setDisable(novo == null);
        });
        inputDeletar.setDisable(true);
        tabelaExames.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
            inputDeletar.setDisable(novo == null);
        });

        tiposExame = tipoExameService.listarTiposExame();

        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        tabelaExames.setItems(tiposExame);
    }

    public void handleTableDoubleClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            handleEditar();
        }
    }

    public void handleEditar() {
        gerarJanela(tabelaExames.getSelectionModel().getSelectedItem());
    }

    public void gerarJanela(TipoExame selecionado) {
        Janela janelaEditarExame = new Janela();
        janelaEditarExame.abrirJanela("/view/TipoExameView.fxml", "Editar Exame", (Stage) tabelaExames.getScene().getWindow(), null);
        if (selecionado != null) {
            tipoExameController = janelaEditarExame.loader.getController();
            tipoExameController.setTipoExame(selecionado);
        }
        janelaEditarExame.stage.setOnHidden(e -> {
            tiposExame.clear();
            tiposExame.setAll(tipoExameService.listarTiposExame());
            tabelaExames.setItems(tiposExame);
        });
    }

    public void handleNovo() {
        gerarJanela(null);
    }

    public void handleDeletar() {
        try {
            tipoExameService.deletarTipoExame(tabelaExames.getSelectionModel().getSelectedItem().getId());
            tiposExame.remove((tabelaExames.getSelectionModel().getSelectedItem()));
            tabelaExames.refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}