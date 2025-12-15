package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Certificado;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.TipoCertificado;
import com.joaonardi.gerenciadorocupacional.service.CertificadoService;
import com.joaonardi.gerenciadorocupacional.service.FuncionarioService;
import com.joaonardi.gerenciadorocupacional.service.SetorService;
import com.joaonardi.gerenciadorocupacional.service.TipoCertificadoService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

import java.time.LocalDate;

public class CertificadoController {
    public ComboBox<Funcionario> inputFuncionario;
    public ComboBox<TipoCertificado> inputTipoCertificado;
    public DatePicker inputDataEmissao;
    public DatePicker inputDataValidade;
    public Button btnSalvar;
    public Button btnCancelar;

    Certificado certificado = null;

    final Janela janela = new Janela();
    final TipoCertificadoService tipoCertificadoService = new TipoCertificadoService();
    final CertificadoService certificadoService = new CertificadoService();
    final FuncionarioService funcionarioService = new FuncionarioService();
    final SetorService setorService = new SetorService();

    @FXML
    private void initialize() {
        inputDataValidade.setEditable(false);
        ObservableList<TipoCertificado> certificados = tipoCertificadoService.listarTiposCertificados();
        funcionarioService.carregarFuncionariosPorStatus(true);
        inputFuncionario.setItems(funcionarioService.listarFuncionarios());
        inputFuncionario.setConverter(new StringConverter<>() {
            @Override
            public String toString(Funcionario funcionario) {
                return funcionario != null ? funcionario.getNome() + " - " + setorService.getSetorMapeado(funcionario.getIdSetor()) : "";
            }

            @Override
            public Funcionario fromString(String s) {
                for (Funcionario f : inputFuncionario.getItems()) {
                    if (f.getNome().equals(s)) {
                        return f;
                    }
                }
                return null;
            }
        });
        inputTipoCertificado.setItems(certificados);
        inputTipoCertificado.setConverter(new StringConverter<>() {
            @Override
            public String toString(TipoCertificado tipoCertificado) {
                return tipoCertificado != null ? tipoCertificado.getNome() + " - " + tipoCertificado.getPeriodicidade() + " Meses" : "";
            }

            @Override
            public TipoCertificado fromString(String s) {
                for (TipoCertificado t : inputTipoCertificado.getItems()) {
                    String tipoCertificadoString = t.getNome() + " - " + t.getPeriodicidade() + " Meses";
                    if (tipoCertificadoString.equalsIgnoreCase(s)) {
                        return t;
                    }
                }
                return null;
            }
        });
        inputTipoCertificado.setValue(certificados.getFirst());
        inputDataEmissao.setValue(LocalDate.now());
        inputFuncionario.setValue(funcionarioService.listarFuncionarios().getFirst());
        inputDataValidade.setValue(certificadoService.calcularValidade(inputDataEmissao.getValue(),inputTipoCertificado.getValue()));
    }

    public void validadeAlteracao() {
        inputDataValidade.setValue(certificadoService.calcularValidade(inputDataEmissao.getValue(),
                inputTipoCertificado.getValue()));
    }


    public void handleSalvar() {
        if (this.certificado == null) {
            this.certificado = Certificado.CertificadoBuilder.builder()
                    .idTipoCertificado(inputTipoCertificado.getValue().getId())
                    .idFuncionario(inputFuncionario.getValue().getId())
                    .dataEmissao(inputDataEmissao.getValue())
                    .dataValidade(inputDataValidade.getValue() == null ? null : inputDataValidade.getValue())
                    .atualizadoPor(null)
                    .build();
            certificadoService.cadastrarCertificado(certificado);
        }
        if (this.certificado != null){
            this.certificado = Certificado.CertificadoBuilder.builder()
                    .id(this.certificado.getId())
                    .idTipoCertificado(inputTipoCertificado.getValue().getId())
                    .idFuncionario(inputFuncionario.getValue().getId())
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
            inputFuncionario.setValue(funcionarioService.getFuncionarioMapeadoPorId(certificado.getIdFuncionario()));
            inputTipoCertificado.setValue(tipoCertificadoService.getTipoCertificadoMapeadoPorId(certificado.getIdTipoCertificado()));
            inputDataEmissao.setValue(certificadoSelecionado.getDataEmissao());
            inputDataValidade.setValue(certificadoSelecionado.getDataEmissao());

        }
    }
}
