package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Exame;
import com.joaonardi.gerenciadorocupacional.model.Periodicidade;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import com.joaonardi.gerenciadorocupacional.service.TipoExameService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class TipoExameController {
    public TextField inputNome;
    public ChoiceBox<Periodicidade> inputPeriodicidade;
    public Button btnCancelar;
    public Button btnSalvar;

    Janela janela = new Janela();

    TipoExameService tipoExameService = new TipoExameService();
    public void initialize() {
        inputPeriodicidade.getItems().addAll(Periodicidade.values());
    }
    public void handleSalvarExame(ActionEvent event) {
        TipoExame tipoExame = TipoExame.TipoExameBuilder.builder()
                .nome(inputNome.getText())
                .periodicidade(inputPeriodicidade.getValue().getValor())
                .build();
        tipoExameService.cadastrarTipoExame(tipoExame);
        janela.fecharJanela(btnSalvar);
    }

    public void handleCancelarExame(ActionEvent event) {
        janela.fecharJanela(btnCancelar);
    }
}
