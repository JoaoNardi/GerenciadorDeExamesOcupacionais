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

public class GerenciarExamesController {
    public TableView<TipoExame> tabelaExames;
    public TableColumn<TipoExame, String> colunaNome;
    public TableColumn<TipoExame, Integer> colunaPerioicidade;
    public Button inputEditar;
    public Button inputNovo;

    ObservableList<TipoExame> tiposExame;
    TipoExameController tipoExameController = new TipoExameController();
    final TipoExameService tipoExameService = new TipoExameService();

    @FXML
    public void initialize() {
        inputEditar.setDisable(true);
        tabelaExames.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
            inputEditar.setDisable(novo == null);
        });

        tiposExame = tipoExameService.listarTiposExame();

        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaPerioicidade.setCellValueFactory(new PropertyValueFactory<>("periodicidade"));
        colunaPerioicidade.setCellFactory(c -> new TableCell<>() {
                    @Override
                    protected void updateItem(Integer periodicidade, boolean empty) {
                        super.updateItem(periodicidade, empty);
                        if (empty || periodicidade == null) {
                            setText(null);
                        } else {
                            Periodicidade periodo = Periodicidade.fromValor(periodicidade);
                            setText(periodo.toString());

                        }
                    }
                }
        );
        tabelaExames.setItems(tiposExame);
    }

    public void handleTableDoubleClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            handleEditar();
        }
    }

    public void handleEditar() {
        TipoExame tipoExameSelecionado = tabelaExames.getSelectionModel().getSelectedItem();
        gerarJanela(tipoExameSelecionado);
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
            tiposExame.addAll(tipoExameService.listarTiposExame());
            tabelaExames.setItems(tiposExame);
        });
    }

    public void handleNovo() {
        gerarJanela(null);
    }
}
