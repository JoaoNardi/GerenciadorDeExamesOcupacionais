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


    public TextField inputArea;
    public ChoiceBox<GrauDeRisco> inputGrauRisco;
    public Button btnCancelar;
    public Button btnSalvar;

    SetorService service = new SetorService();
    Janela janela = new Janela();
    private Setor setor;
    @FXML
    public void initialize() {
        inputGrauRisco.getItems().addAll(GrauDeRisco.values());
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
        if (setor !=null){
            inputArea.setText(setor.getArea());
            inputGrauRisco.setValue(GrauDeRisco.values()[setor.getGrauRisco()]);
        }
    }

    @FXML
    public void handleSalvarSetor(ActionEvent event) {
        if (setor == null || setor.getId() ==null){
        setor = Setor.SetorBuilder.builder()
                .id(null)
                .area(inputArea.getText())
                .grauRisco(inputGrauRisco.getValue().getValor()).build();
        } else {
            setor = Setor.SetorBuilder.builder()
                    .id(this.setor.getId())
                    .area(inputArea.getText())
                    .grauRisco(inputGrauRisco.getValue().getValor())
                    .build();
        }
        service.cadastrarSetor(setor);
        janela.fecharJanela(btnSalvar);
    }
    @FXML
    public void handleCancelarSetor(ActionEvent event) {
        janela.fecharJanela(btnCancelar);

    }

}
