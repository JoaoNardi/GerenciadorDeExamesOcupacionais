package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Exame;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import com.joaonardi.gerenciadorocupacional.service.ExameService;
import com.joaonardi.gerenciadorocupacional.service.FuncionarioService;
import com.joaonardi.gerenciadorocupacional.service.TipoExameService;
import com.joaonardi.gerenciadorocupacional.util.ComboBoxCustom;
import com.joaonardi.gerenciadorocupacional.util.Editavel;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;

import java.util.List;

public class ExamesController extends Janela<Exame> implements Editavel<Exame> {
    public ComboBoxCustom<Funcionario> inputFuncionario;
    public ComboBoxCustom<TipoExame> inputTipoExame;
    public DatePicker inputDataEmissao;
    public DatePicker inputDataValidade;
    public Button btnSalvar;
    public Button btnCancelar;
    final Janela janela = new Janela();
    final FuncionarioService funcionarioService = new FuncionarioService();
    final TipoExameService tipoExameService = new TipoExameService();
    final ExameService exameService = new ExameService();
    Exame exame = null;

    @FXML
    private void initialize() {
        funcionarioService.listarFuncionariosPorStatus(true);
        inputFuncionario.setItemsAndDisplay(funcionarioService.listarFuncionariosPorStatus(true), List.of(Funcionario::getNome,
                f -> f.getSetor().getArea()));

        inputTipoExame.setItemsAndDisplay(tipoExameService.listarTiposExame(), List.of(TipoExame::getNome));
        inputDataValidade.setEditable(false);
        setBindings();
    }

    private void setBindings() {
        BooleanBinding inputsValidos =
                inputFuncionario.valueProperty().isNotNull()
                        .and(inputTipoExame.valueProperty().isNotNull())
                        .and(inputDataEmissao.valueProperty().isNotNull())
                        .and(inputDataValidade.valueProperty().isNotNull());
        btnSalvar.disableProperty().bind(inputsValidos.not());
    }

    public void handleSalvarExame() {
        if (this.exame == null) {
            this.exame = Exame.ExameBuilder.builder()
                    .id(null)
                    .tipoExame(inputTipoExame.getValue())
                    .funcionario(inputFuncionario.getValue())
                    .dataEmissao(inputDataEmissao.getValue())
                    .dataValidade(inputDataValidade.getValue() == null ? null : inputDataValidade.getValue())
                    .atualizadoPor(null)
                    .build();
            exameService.lancarExame(exame);
        }
        if (this.exame != null || this.exame.getId() != null || this.exame.getAtualizadoPor() == null) { // editar exame que nao é nao atualizado ainda
            Exame exame1 = Exame.ExameBuilder.builder()
                    .id(this.exame.getId())
                    .tipoExame(inputTipoExame.getValue())
                    .funcionario(inputFuncionario.getValue())
                    .dataEmissao(inputDataEmissao.getValue())
                    .dataValidade(inputDataValidade.getValue() == null ? null : inputDataValidade.getValue())
                    .atualizadoPor(null)
                    .build();
            exameService.editarExame(exame1);
        }
        janela.fecharJanela(btnSalvar);
    }

    public void handleCancelarExame() {
        janela.fecharJanela(btnCancelar);
    }

    public void validadeAlteracao() {
        if (inputFuncionario.getValue() != null && inputTipoExame.getValue() != null) {
            inputDataValidade.setValue(exameService.calcularValidadeExame(inputFuncionario.getValue(), inputDataEmissao.getValue(),
                    inputTipoExame.getValue()));
        }
    }

    @Override
    public void set(Exame objeto) {
        super.set(objeto);
        if (exame != null) {
            this.exame = objeto;
            inputFuncionario.setValue(objeto.getFuncionario());
            inputTipoExame.setValue(objeto.getTipoExame());
            inputDataEmissao.setValue(objeto.getDataEmissao());
            inputDataValidade.setValue(objeto.getDataEmissao());
        }
        inputDataValidade.setValue(exameService.calcularValidadeExame(inputFuncionario.getValue(), inputDataEmissao.getValue(), inputTipoExame.getValue()));
    }

}
