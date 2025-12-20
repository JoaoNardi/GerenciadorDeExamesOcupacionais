package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Periodicidade;
import com.joaonardi.gerenciadorocupacional.model.TipoCertificado;
import com.joaonardi.gerenciadorocupacional.service.TipoCertificadoService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class TipoCertificadoController {
    public TextField inputNome;
    public ChoiceBox<Periodicidade> inputPeriodicidade;
    public Button btnCancelar;
    public Button btnSalvar;

    final Janela janela = new Janela();
    private TipoCertificado tipoCertificado;
    final TipoCertificadoService tipoCertificadoService = new TipoCertificadoService();

    public void initialize() {
        inputPeriodicidade.getItems().addAll(Periodicidade.values());
        setBindings();
    }

    private void setBindings(){
        BooleanBinding inputsValidos =
                inputNome.textProperty().isNotNull()
                        .and(inputPeriodicidade.valueProperty().isNotNull());
        btnSalvar.disableProperty().bind(inputsValidos.not());
    }

    public void setTipoCertificado(TipoCertificado tipoCertificadoSelecionado) {
        this.tipoCertificado = tipoCertificadoSelecionado;
        if (tipoCertificado != null) {

            inputNome.setText(tipoCertificado.getNome());
            inputPeriodicidade.setValue(Periodicidade.fromValor(tipoCertificadoSelecionado.getPeriodicidade()));
        }

    }

    @FXML
    public void handleSalvarCertificado() {
        if (tipoCertificado == null || tipoCertificado.getId() == null) {
            tipoCertificado = TipoCertificado.TipoCertificadoBuilder.builder()
                    .id(null)
                    .nome(inputNome.getText())
                    .periodicidade(inputPeriodicidade.getValue().getValor())
                    .build();
        } else {
            tipoCertificado = TipoCertificado.TipoCertificadoBuilder.builder()
                    .id(this.tipoCertificado.getId())
                    .nome(inputNome.getText())
                    .periodicidade(inputPeriodicidade.getValue().getValor())
                    .build();
        }
        tipoCertificadoService.cadastrarTipoCertificado(tipoCertificado);
        janela.fecharJanela(btnSalvar);
    }

    public void handleCancelarCertificado() {
        janela.fecharJanela(btnCancelar);
    }


}
