package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Particularidade;
import com.joaonardi.gerenciadorocupacional.model.Periodicidade;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import com.joaonardi.gerenciadorocupacional.service.ParticularidadeService;
import com.joaonardi.gerenciadorocupacional.service.TipoExameService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class ParticularidadeController extends Janela {
    public TextField inputNome;
    public TextArea inputDescricao;
    public ChoiceBox<TipoExame> inputTipoExame;
    public ChoiceBox<Periodicidade> inputPeriodicidade;
    public Button btnSalvar;
    public Button btnCancelar;
    Particularidade particularidade;
    TipoExameService tipoExameService = new TipoExameService();
    public ParticularidadeService particularidadeService = new ParticularidadeService();

    @FXML
    private void initialize() {
        tipoExameService.carregarTipoExames();
        inputTipoExame.getItems().setAll(tipoExameService.listarTiposExame());
        inputTipoExame.setConverter(new StringConverter<>() {
            @Override
            public String toString(TipoExame tipoExame) {
                return tipoExame != null ? tipoExame.getNome() : "";
            }

            @Override
            public TipoExame fromString(String s) {
                for (TipoExame tipoExame: inputTipoExame.getItems()){
                    String tipoE = tipoExame.getNome();
                    if (tipoE.equals(s)){
                        return tipoExame;
                    }
                }
                return null;
            }
        });
        inputPeriodicidade.getItems().setAll(Periodicidade.values());
    }

    public void setParticularidade(Particularidade particularidade) {
        this.particularidade = particularidade;
        inputNome.setText(particularidade.getNome());
        inputDescricao.setText(particularidade.getDescricao());
        inputTipoExame.setValue(particularidade.getTipoExame());
        inputPeriodicidade.setValue(Periodicidade.fromValor(particularidade.getPeriodicidade()));
    }

    public void handleSalvarParticularidade(ActionEvent event) {
        String contexto = null;
        if (this.particularidade== null) {
            contexto = "salvar";
        } else {
            contexto = "atualizar";
        }
        salvar(contexto,
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
