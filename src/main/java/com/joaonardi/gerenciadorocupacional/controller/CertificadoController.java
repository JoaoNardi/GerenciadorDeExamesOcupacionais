package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Certificado;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.TipoCertificado;
import com.joaonardi.gerenciadorocupacional.service.CertificadoService;
import com.joaonardi.gerenciadorocupacional.service.FuncionarioService;
import com.joaonardi.gerenciadorocupacional.service.TipoCertificadoService;
import com.joaonardi.gerenciadorocupacional.util.ComboBoxCustom;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;
import java.util.List;

public class CertificadoController {
    public ComboBoxCustom<Funcionario> inputFuncionario;
    public ComboBoxCustom<TipoCertificado> inputTipoCertificado;
    public DatePicker inputDataEmissao;
    public DatePicker inputDataValidade;
    public Button btnSalvar;
    public Button btnCancelar;

    Certificado certificado = null;

    final Janela janela = new Janela();
    final TipoCertificadoService tipoCertificadoService = new TipoCertificadoService();
    final CertificadoService certificadoService = new CertificadoService();
    final FuncionarioService funcionarioService = new FuncionarioService();

    @FXML
    private void initialize() {
        inputDataValidade.setEditable(false);
        funcionarioService.carregarFuncionariosPorStatus(true);
        tipoCertificadoService.carregarTiposCertificado();
        inputFuncionario.setItemsAndDisplay(funcionarioService.listarFuncionarios(),List.of(Funcionario::getNome, f -> f.getSetor().getArea()));
        inputTipoCertificado.setItemsAndDisplay(tipoCertificadoService.listarTiposCertificados(), List.of(TipoCertificado::getNome));
        inputDataEmissao.setValue(LocalDate.now());
    }

    public void validadeAlteracao() {
        inputDataValidade.setValue(certificadoService.calcularValidade(inputDataEmissao.getValue(),
                inputTipoCertificado.getValue()));
    }


    public void handleSalvar() {
        if (this.certificado == null) {
            Certificado certificado = Certificado.CertificadoBuilder.builder()
                    .tipoCertificado(inputTipoCertificado.getValue())
                    .funcionario(inputFuncionario.getValue())
                    .dataEmissao(inputDataEmissao.getValue())
                    .dataValidade(inputDataValidade.getValue() == null ? null : inputDataValidade.getValue())
                    .atualizadoPor(null)
                    .build();
            certificadoService.cadastrarCertificado(certificado);
            this.certificado = certificado;
        }
        if (this.certificado.getId() != null){
            Certificado certificado = Certificado.CertificadoBuilder.builder()
                    .id(this.certificado.getId())
                    .tipoCertificado(inputTipoCertificado.getValue())
                    .funcionario(inputFuncionario.getValue())
                    .dataEmissao(inputDataEmissao.getValue())
                    .dataValidade(inputDataValidade.getValue())
                    .atualizadoPor(null)
                    .build();
            certificadoService.editarCertificado(certificado);
        }
        janela.fecharJanela(btnSalvar);
    }

    public void handleCancelar() {
        janela.fecharJanela(btnCancelar);
    }

    public void setCertificado(Certificado certificadoSelecionado) {
        this.certificado = certificadoSelecionado;
        if (certificado != null) {
            inputFuncionario.setValue(certificado.getFuncionario());
            inputTipoCertificado.setValue(certificado.getTipoCertificado());
            inputDataEmissao.setValue(certificadoSelecionado.getDataEmissao());
            inputDataValidade.setValue(certificadoSelecionado.getDataEmissao());

        }
    }
}
