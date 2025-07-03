package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.*;
import com.joaonardi.gerenciadorocupacional.service.TipoExameService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class TipoExameController {
    public TextField inputNome;
    public ChoiceBox<Periodicidade> inputPeriodicidade;
    public Button btnCancelar;
    public Button btnSalvar;

    Janela janela = new Janela();
    private TipoExame tipoExame;
    TipoExameService tipoExameService = new TipoExameService();

    public void initialize() {
        inputPeriodicidade.getItems().addAll(Periodicidade.values());
    }

    public void setTipoExame(TipoExame tipoExameSelecionado) {
        this.tipoExame = tipoExameSelecionado;
        if (tipoExame !=null){

            inputNome.setText(tipoExame.getNome());
            inputPeriodicidade.setValue(Periodicidade.fromValor(tipoExameSelecionado.getPeriodicidade()));
        }

    }
    @FXML
    public void handleSalvarExame(ActionEvent event) {
        if (tipoExame == null || tipoExame.getId() ==null){
            tipoExame = TipoExame.TipoExameBuilder.builder()
                    .id(null)
                    .nome(inputNome.getText())
                    .periodicidade(inputPeriodicidade.getValue().getValor())
                    .build();
        } else {
            tipoExame = TipoExame.TipoExameBuilder.builder()
                    .id(this.tipoExame.getId())
                    .nome(inputNome.getText())
                    .periodicidade(inputPeriodicidade.getValue().getValor())
                    .build();
        }
        tipoExameService.cadastrarTipoExame(tipoExame);
        janela.fecharJanela(btnSalvar);
    }

    public void handleCancelarExame(ActionEvent event) {
        janela.fecharJanela(btnCancelar);
    }


}
