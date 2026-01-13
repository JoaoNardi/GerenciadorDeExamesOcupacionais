package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.Particularidade;
import com.joaonardi.gerenciadorocupacional.model.VinculoFuncionarioParticularidade;
import com.joaonardi.gerenciadorocupacional.service.FuncionarioService;
import com.joaonardi.gerenciadorocupacional.service.ParticularidadeService;
import com.joaonardi.gerenciadorocupacional.util.ComboBoxCustom;
import com.joaonardi.gerenciadorocupacional.util.DatePickerCustom;
import com.joaonardi.gerenciadorocupacional.util.Editavel;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.util.List;

public class VinculoParticularidadeController extends Janela<VinculoFuncionarioParticularidade> implements Editavel<VinculoFuncionarioParticularidade> {

    private final ParticularidadeService particularidadeService = new ParticularidadeService();
    private final FuncionarioService funcionarioService = new FuncionarioService();
    public Button btnCancelar;
    public Button btnSalvar;
    public ComboBoxCustom<Particularidade> inputParticularidade;
    public ComboBoxCustom<Funcionario> inputFuncionario;
    public TextArea inputMotivo;
    public DatePickerCustom inputInclusao;
    public DatePickerCustom inputExclusao;
    public Label outputPeriodicidade;

    private VinculoFuncionarioParticularidade vinculoFuncionarioParticularidade;

    public void initialize() {
        inputParticularidade.setItemsAndDisplay(particularidadeService.listarTodasParticularidades(), List.of(Particularidade::getNome,
                p -> p.getTipoExame().getNome()));
        funcionarioService.listarFuncionariosPorStatus(true);
        inputFuncionario.setItemsAndDisplay(funcionarioService.listarFuncionariosPorStatus(true), List.of(Funcionario::getNome, f -> f.getSetor().getArea()));
        setBindings();
    }

    private void setBindings() {
        BooleanBinding inputsValidos =
                inputFuncionario.valueProperty().isNotNull()
                        .and(inputParticularidade.valueProperty().isNotNull())
                        .and(inputMotivo.textProperty().isNotNull()
                                .and(inputInclusao.valueProperty().isNotNull()));
        btnSalvar.disableProperty().bind(inputsValidos.not());
    }

    public void handleSalvarVinculo() {
        String acao = "";
        if (this.vinculoFuncionarioParticularidade == null || this.vinculoFuncionarioParticularidade.getId() == null) {
            acao = "salvo";
            this.vinculoFuncionarioParticularidade =
                    VinculoFuncionarioParticularidade.VinculoFuncionarioParticularidadeBuilder.builder()
                            .funcionario(inputFuncionario.getValue())
                            .particularidade(inputParticularidade.getValue())
                            .motivo(inputMotivo.getText())
                            .dataInclusao(inputInclusao.getValue())
                            .dataExclusao(inputExclusao.getValue())
                            .build();
        } else {
            acao = "atualizado";
            this.vinculoFuncionarioParticularidade =
                    VinculoFuncionarioParticularidade.VinculoFuncionarioParticularidadeBuilder.builder()
                            .id(this.vinculoFuncionarioParticularidade.getId())
                            .funcionario(inputFuncionario.getValue())
                            .particularidade(inputParticularidade.getValue())
                            .motivo(inputMotivo.getText())
                            .dataInclusao(inputInclusao.getValue())
                            .dataExclusao(inputExclusao.getValue())
                            .build();
        }

        salvar("Vinculo", acao,
                btnSalvar,
                () -> particularidadeService.vincularFuncionarioParticularidade(this.vinculoFuncionarioParticularidade
                ));
    }

    public void handleCancelarVinculo() {
        fecharJanela(btnCancelar);
    }

    @Override
    public void set(VinculoFuncionarioParticularidade objeto) {
        super.set(objeto);
        Platform.runLater(() -> {
            if (objeto != null) {
                vinculoFuncionarioParticularidade = objeto;
                inputFuncionario.setValue(objeto.getFuncionario());
                inputParticularidade.setValue(objeto.getParticularidade());
                inputMotivo.setText(objeto.getMotivo());
                inputInclusao.setValue(objeto.getDataInclusao());
                inputExclusao.setValue(objeto.getDataExclusao());
                outputPeriodicidade.setText(objeto.getParticularidade().getPeriodicidade().toString() + " meses");
                if (inputFuncionario.getValue() != null) {
                    inputFuncionario.setDisable(true);
                }
                if (inputParticularidade.getValue() != null) {
                    inputParticularidade.setDisable(true);
                }
            }
        });

    }
}
