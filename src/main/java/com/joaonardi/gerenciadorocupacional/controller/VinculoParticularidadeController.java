package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.Particularidade;
import com.joaonardi.gerenciadorocupacional.model.VinculoFuncionarioParticularidade;
import com.joaonardi.gerenciadorocupacional.service.FuncionarioService;
import com.joaonardi.gerenciadorocupacional.service.ParticularidadeService;
import com.joaonardi.gerenciadorocupacional.util.ComboBoxCustom;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.util.List;

public class VinculoParticularidadeController extends Janela {

    private final ParticularidadeService particularidadeService = new ParticularidadeService();
    private final FuncionarioService funcionarioService = new FuncionarioService();
    public Button btnCancelar;
    public Button btnSalvar;
    public ComboBoxCustom<Particularidade> inputParticularidade;
    public ComboBoxCustom<Funcionario> inputFuncionario;
    public TextArea inputMotivo;

    public void initialize() {
        inputParticularidade.setItemsAndDisplay(particularidadeService.listarTodasParticularidades(), List.of(Particularidade::getNome,
                p -> p.getTipoExame().getNome()));
        funcionarioService.listarFuncionariosPorStatus(true);
        inputFuncionario.setItemsAndDisplay(funcionarioService.listarFuncionariosPorStatus(true), List.of(Funcionario::getNome, f -> f.getSetor().getArea()));
        setBindings();
        Platform.runLater(() -> {
            setVinculo((VinculoFuncionarioParticularidade) this.objetoPrincipal);
        });
    }

    private void setBindings() {
        BooleanBinding inputsValidos =
                inputFuncionario.valueProperty().isNotNull()
                        .and(inputParticularidade.valueProperty().isNotNull())
                        .and(inputMotivo.textProperty().isNotNull());
        btnSalvar.disableProperty().bind(inputsValidos.not());
    }

    public void handleSalvarVinculo() {
        salvar("Vinculo", "Salvo",
                btnSalvar,
                () -> particularidadeService.vincularFuncionarioParticularidade(inputFuncionario.getValue(),
                        inputParticularidade.getValue(),
                        inputMotivo.getText()));
    }

    public void handleCancelarVinculo() {
        fecharJanela(btnCancelar);
    }

    public void setVinculo(VinculoFuncionarioParticularidade vinculoFuncionarioParticularidade) {
        if (vinculoFuncionarioParticularidade != null) {
            inputFuncionario.setValue(vinculoFuncionarioParticularidade.getFuncionario());
            inputParticularidade.setValue(vinculoFuncionarioParticularidade.getParticularidade());
            inputMotivo.setText(vinculoFuncionarioParticularidade.getMotivo());
        }
    }
}
