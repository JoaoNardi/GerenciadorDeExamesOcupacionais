package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Setor;
import com.joaonardi.gerenciadorocupacional.service.SetorService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;


public class SetorController {

    public TextField inputArea;
    public Button btnCancelar;
    public Button btnSalvar;

    final SetorService service = new SetorService();
    final Janela janela = new Janela();
    private Setor setor;

    @FXML
    private void initialize() {
        btnSalvar.disableProperty().bind(inputArea.textProperty().isNotNull().not());
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
        if (setor != null) {
            inputArea.setText(setor.getArea());
        }
    }

    @FXML
    public void handleSalvarSetor() {
        if (setor == null || setor.getId() == null) {
            setor = Setor.SetorBuilder.builder()
                    .id(null)
                    .area(inputArea.getText())
                    .build();
        } else {
            setor = Setor.SetorBuilder.builder()
                    .id(this.setor.getId())
                    .area(inputArea.getText())
                    .build();
        }
        service.cadastrarSetor(setor);
        janela.fecharJanela(btnSalvar);
    }

    @FXML
    public void handleCancelarSetor() {
        janela.fecharJanela(btnCancelar);
    }

}
