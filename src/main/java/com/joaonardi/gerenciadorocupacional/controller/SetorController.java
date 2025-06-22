package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.GrauDeRisco;
import com.joaonardi.gerenciadorocupacional.model.Setor;
import com.joaonardi.gerenciadorocupacional.service.SetorService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;


public class SetorController {


    public TextField InputArea;
    public ChoiceBox<GrauDeRisco> InputGrauRisco;
    public Button btnCancelar;
    public Button btnSalvar;

    SetorService service = new SetorService();
    Janela janela = new Janela();
    @FXML
    public void initialize() {
        InputGrauRisco.getItems().addAll(GrauDeRisco.values());
    }

    @FXML
    public void handleSalvarSetor(ActionEvent event) {
        Setor setor = Setor.SetorBuilder.builder()
                .area(InputArea.getText())
                .grauRisco(InputGrauRisco.getValue().getValor()).build();
        service.cadastrarSetor(setor);
        janela.fecharJanela(btnSalvar);
    }
    @FXML
    public void handleCancelarSetor(ActionEvent event) {
        janela.fecharJanela(btnCancelar);

    }
}
