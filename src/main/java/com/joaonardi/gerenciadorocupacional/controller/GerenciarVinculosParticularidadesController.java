package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.MainApp;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.Particularidade;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import com.joaonardi.gerenciadorocupacional.model.VinculoFuncionarioParticularidade;
import com.joaonardi.gerenciadorocupacional.service.ParticularidadeService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.swing.*;

public class GerenciarVinculosParticularidadesController extends Janela {


    public TableView<VinculoFuncionarioParticularidade> tabelaVinculos;
    public TableColumn<VinculoFuncionarioParticularidade, String> colunaFuncionario;
    public TableColumn<VinculoFuncionarioParticularidade, String> colunaParticularidade;
    public TableColumn<VinculoFuncionarioParticularidade, String> colunaTipoExame;
    public TableColumn<VinculoFuncionarioParticularidade, String> colunaMotivo;

    ParticularidadeService particularidadeService = new ParticularidadeService();

    @FXML
    private void initialize() {
        colunaFuncionario.setCellValueFactory(f ->
                new SimpleObjectProperty<>(f.getValue().getFuncionario().getNome()));
        colunaParticularidade.setCellValueFactory(f ->
                new SimpleStringProperty(f.getValue().getParticularidade().getNome()));
        colunaTipoExame.setCellValueFactory(f ->
                new SimpleStringProperty(f.getValue().getParticularidade().getTipoExame().getNome()));
        colunaMotivo.setCellValueFactory(f ->
                new SimpleStringProperty(f.getValue().getMotivo()));
        tabelaVinculos.setItems(particularidadeService.listarTodosVinculos());
    }

    public void handleEditar() {
        VinculoFuncionarioParticularidade vinculoFuncionarioParticularidade = tabelaVinculos.getSelectionModel().getSelectedItem();
        if (vinculoFuncionarioParticularidade != null) {
            Janela janelaEditarFuncionario = new Janela();
            janelaEditarFuncionario.abrirJanela("/view/VinculoParticularidadesFuncionarios.fxml", "Editar Vinculo", (Stage) tabelaVinculos.getScene().getWindow(),
                    null);
            VinculoParticularidadeController vinculoFuncionarioParticularidadeController = janelaEditarFuncionario.loader.getController();
            vinculoFuncionarioParticularidadeController.setVinculo(vinculoFuncionarioParticularidade);
        }
    }

    public void handleDeletar() {
        try {
            particularidadeService.desvincularParticularidadeFuncionario(tabelaVinculos.getSelectionModel().getSelectedItem());
            tabelaVinculos.setItems(particularidadeService.listarTodosVinculos());
            tabelaVinculos.refresh();
            JOptionPane.showMessageDialog(null, "Vinculo deletado com sucesso");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        tabelaVinculos.refresh();
    }

    public void handleTableDoubleClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            handleEditar();
        }
    }

    public void handleAdicionar(ActionEvent event) {

    }
}
