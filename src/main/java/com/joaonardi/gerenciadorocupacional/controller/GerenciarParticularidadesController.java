package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Particularidade;
import com.joaonardi.gerenciadorocupacional.service.ParticularidadeService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class GerenciarParticularidadesController {
    public TableView<Particularidade> tabelaParticularidades;
    public TableColumn<Particularidade, String> colunaNome;
    public TableColumn<Particularidade, Integer> colunaPeriodicidade;
    public ParticularidadeService particularidadeService = new ParticularidadeService();
    public TableColumn<Particularidade, String> colunaTipoExame;
    public ParticularidadeController particularidadeController = new ParticularidadeController();


    @FXML
    private void initialize() {
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaTipoExame.setCellValueFactory(f -> {
            return new SimpleObjectProperty<>(f.getValue().getTipoExame().getNome());
        });
        colunaPeriodicidade.setCellValueFactory(new PropertyValueFactory<>("periodicidade"));
        particularidadeService.carregarTodasParticularidades();
        tabelaParticularidades.setItems(particularidadeService.listarParticularidades());
    }

    @FXML
    private void handleTableDoubleClick(javafx.scene.input.MouseEvent mouseEvent) {
        if (mouseEvent.getTarget() instanceof TableCell<?,?>){
            if (mouseEvent.getClickCount() == 2) {
                handleEditar();
            }
        }

    }
    public void handleEditar() {
        Particularidade particularidadeSelecionada = tabelaParticularidades.getSelectionModel().getSelectedItem();
        if (particularidadeSelecionada != null) {
            Janela janelaEditarFuncionario = new Janela();
            janelaEditarFuncionario.abrirJanela("/view/ParticularidadeView.fxml", "Editar Particularidade",
                    (Stage) tabelaParticularidades.getScene().getWindow(), null);
            particularidadeController = janelaEditarFuncionario.loader.getController();
            particularidadeController.setParticularidade(particularidadeSelecionada);
        }
    }

    public void handleNovo(ActionEvent event) {
    }
}
