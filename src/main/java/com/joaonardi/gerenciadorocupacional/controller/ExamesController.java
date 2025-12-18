package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Exame;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import com.joaonardi.gerenciadorocupacional.service.ExameService;
import com.joaonardi.gerenciadorocupacional.service.FuncionarioService;
import com.joaonardi.gerenciadorocupacional.service.TipoExameService;
import com.joaonardi.gerenciadorocupacional.util.ComboBoxCustom;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;
import java.util.List;

public class ExamesController {
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
        funcionarioService.carregarFuncionariosPorStatus(true);
        inputFuncionario.setItemsAndDisplay(funcionarioService.listarFuncionarios(), List.of(Funcionario::getNome, f -> f.getSetor().getArea()));

        inputTipoExame.setItemsAndDisplay(tipoExameService.listarTiposExame(), List.of(TipoExame::getNome));

        inputDataEmissao.setValue(LocalDate.now());
    }

    public void handleSalvarExame(ActionEvent event) {
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

    public void handleCancelarExame(ActionEvent event) {
        janela.fecharJanela(btnCancelar);
    }

    public void validadeAlteracao(ActionEvent event) {
        if (inputFuncionario.getValue() != null && inputTipoExame.getValue() != null) {
            inputDataValidade.setValue(exameService.calcularValidadeExame(inputFuncionario.getValue(), inputDataEmissao.getValue(),
                    inputTipoExame.getValue()));
        }
    }

    public void setExame(Exame exameSelecionado) {
        this.exame = exameSelecionado;
        if (exame != null) {
            inputFuncionario.setValue(exame.getFuncionario());
            inputTipoExame.setValue(exame.getTipoExame());
            inputDataEmissao.setValue(exameSelecionado.getDataEmissao());
            inputDataValidade.setValue(exameSelecionado.getDataEmissao());
        }
        inputDataValidade.setValue(exameService.calcularValidadeExame(inputFuncionario.getValue(), inputDataEmissao.getValue(), inputTipoExame.getValue()));
    }
}
