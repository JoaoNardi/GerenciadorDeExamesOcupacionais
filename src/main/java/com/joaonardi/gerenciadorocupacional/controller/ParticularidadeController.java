package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Particularidade;
import com.joaonardi.gerenciadorocupacional.model.Periodicidade;
import com.joaonardi.gerenciadorocupacional.model.Setor;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import com.joaonardi.gerenciadorocupacional.service.ParticularidadeService;
import com.joaonardi.gerenciadorocupacional.service.TipoExameService;
import com.joaonardi.gerenciadorocupacional.util.ComboBoxCustom;
import com.joaonardi.gerenciadorocupacional.util.Editavel;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.List;

public class ParticularidadeController extends Janela<Particularidade> implements Editavel<Particularidade> {
    public TextField inputNome;
    public TextArea inputDescricao;
    public ComboBoxCustom<TipoExame> inputTipoExame;
    public ChoiceBox<Periodicidade> inputPeriodicidade;
    public Button btnSalvar;
    public Button btnCancelar;
    Particularidade particularidade;
    TipoExameService tipoExameService = new TipoExameService();
    public ParticularidadeService particularidadeService = new ParticularidadeService();

    @FXML
    private void initialize() {
        inputTipoExame.setItemsAndDisplay(tipoExameService.listarTiposExame(), List.of(TipoExame::getNome));
        inputPeriodicidade.getItems().setAll(Periodicidade.values());
        setBindings();
    }

    @Override
    public void set(Particularidade objeto) {
        super.set(objeto);
        if (objeto != null) {
            inputNome.setText(objeto.getNome());
            inputDescricao.setText(objeto.getDescricao());
            inputTipoExame.setValue(objeto.getTipoExame());
            inputPeriodicidade.setValue(Periodicidade.fromValor(objeto.getPeriodicidade()));
        }
    }

    private void setBindings() {
        BooleanBinding inputsValidos =
                inputNome.textProperty().isNotNull()
                        .and(inputTipoExame.valueProperty().isNotNull())
                        .and(inputPeriodicidade.valueProperty().isNotNull());
        btnSalvar.disableProperty().bind(inputsValidos.not());
    }

    public void handleSalvarParticularidade(ActionEvent event) {
        String acao = "";
        if (this.particularidade == null) {
            acao = "salvo";
        } else {
            acao = "atualizado";
        }
        salvar("Particularidade", acao,
                btnSalvar,
                () -> {
                    Particularidade particularidade = Particularidade.ParticularidadeBuilder.builder()
                            .id(this.particularidade == null ? null : this.particularidade.getId())
                            .nome(inputNome.getText())
                            .descricao(inputDescricao.getText())
                            .tipoExame(inputTipoExame.getValue())
                            .periodicidade(inputPeriodicidade.getValue().getValor())
                            .build();
                    particularidadeService.cadastrarParticularidade(particularidade);
                });
    }

    public void handleCancelarParticularidade(ActionEvent event) {
        fecharJanela(btnCancelar);
    }
}
