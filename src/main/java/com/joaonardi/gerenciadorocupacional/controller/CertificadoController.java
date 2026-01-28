package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Certificado;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.TipoCertificado;
import com.joaonardi.gerenciadorocupacional.service.CertificadoService;
import com.joaonardi.gerenciadorocupacional.service.FuncionarioService;
import com.joaonardi.gerenciadorocupacional.service.TipoCertificadoService;
import com.joaonardi.gerenciadorocupacional.util.ComboBoxCustom;
import com.joaonardi.gerenciadorocupacional.util.DatePickerCustom;
import com.joaonardi.gerenciadorocupacional.util.Editavel;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.time.LocalDate;
import java.util.List;

public class CertificadoController extends Janela<Certificado> implements Editavel<Certificado> {
    public ComboBoxCustom<Funcionario> inputFuncionario;
    public ComboBoxCustom<TipoCertificado> inputTipoCertificado;
    public DatePickerCustom inputDataEmissao;
    public DatePickerCustom inputDataValidade;
    public Button btnSalvar;
    public Button btnCancelar;

    Certificado certificado = null;

    final Janela<Certificado> janela = new Janela<>();
    final TipoCertificadoService tipoCertificadoService = new TipoCertificadoService();
    final CertificadoService certificadoService = new CertificadoService();
    final FuncionarioService funcionarioService = new FuncionarioService();

    @FXML
    private void initialize() {
        inputDataValidade.setEditable(false);

        inputFuncionario.setItemsAndDisplay(funcionarioService.listarFuncionariosPorStatus(true), List.of(Funcionario::getNome,
                f -> f.getSetor().getArea()));
        inputTipoCertificado.setItemsAndDisplay(tipoCertificadoService.listarTiposCertificados(), List.of(TipoCertificado::getNome));
        Platform.runLater(()->  inputDataEmissao.setValue(LocalDate.now()));
        setBindings();
    }

    private void setBindings() {
        BooleanBinding inputsValidos =
                inputFuncionario.valueProperty().isNotNull()
                        .and(inputTipoCertificado.valueProperty().isNotNull())
                        .and(inputDataEmissao.valueProperty().isNotNull())
                        .and(inputDataValidade.valueProperty().isNotNull());
        btnSalvar.disableProperty().bind(inputsValidos.not());
    }

    public void validadeAlteracao() {
        Platform.runLater(()->  inputDataValidade.setValue(certificadoService.calcularValidade(inputDataEmissao.getValue(),
                inputTipoCertificado.getValue())));
    }

    public void handleSalvar() {
        String acao = "";
        if (this.certificado == null || this.certificado.getId() == null) {
            acao = "salvo";
            this.certificado = Certificado.CertificadoBuilder.builder()
                    .id(null)
                    .tipoCertificado(inputTipoCertificado.getValue())
                    .funcionario(inputFuncionario.getValue())
                    .dataEmissao(inputDataEmissao.getValue())
                    .dataValidade(inputDataValidade.getValue() == null ? null : inputDataValidade.getValue())
                    .atualizadoPor(null)
                    .build();
        } else {
            acao = "atualizado";
            this.certificado = Certificado.CertificadoBuilder.builder()
                    .id(objetoPrincipal.getId())
                    .tipoCertificado(inputTipoCertificado.getValue())
                    .funcionario(inputFuncionario.getValue())
                    .dataEmissao(inputDataEmissao.getValue())
                    .dataValidade(inputDataValidade.getValue())
                    .atualizadoPor(null)
                    .build();
        }
        salvar("Certificado", acao, btnSalvar, () -> certificadoService.cadastrarCertificado(this.certificado));
        janela.fecharJanela(btnSalvar);
    }

    public void handleCancelar() {
        janela.fecharJanela(btnCancelar);
    }

    @Override
    public void set(Certificado objeto) {
        super.set(objeto);
        if (objeto != null) {
            Platform.runLater(()->{
                this.certificado = objeto;
                inputTipoCertificado.setDisable(true);
                inputFuncionario.setValue(objeto.getFuncionario());
                inputTipoCertificado.setValue(objeto.getTipoCertificado());
                inputDataEmissao.setValue(objeto.getDataEmissao());
                inputDataValidade.setValue(objeto.getDataEmissao());
            });
        }
    }
}
