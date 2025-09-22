package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.cache.FuncionarioCache;
import com.joaonardi.gerenciadorocupacional.cache.SetorCache;
import com.joaonardi.gerenciadorocupacional.model.Certificado;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.TipoCertificado;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import com.joaonardi.gerenciadorocupacional.service.CertificadoService;
import com.joaonardi.gerenciadorocupacional.service.TipoCertificadoService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.controlsfx.control.Notifications;

import java.time.LocalDate;

public class CertificadoController {
    public ComboBox<Funcionario> inputFuncionario;
    public ComboBox<TipoCertificado> inputTipoCertificado;
    public DatePicker inputDataEmissao;
    public DatePicker inputDataValidade;

    Janela janela = new Janela();
    TipoCertificadoService tipoCertificadoService = new TipoCertificadoService();
    CertificadoService certificadoService = new CertificadoService();

    @FXML
    private void initialize() throws Exception {
        inputDataValidade.setEditable(false);
        ObservableList<TipoCertificado> certificados = tipoCertificadoService.listarTiposCertificados();
        FuncionarioCache.carregarFuncionarios(true);
        ObservableList<Funcionario> funcionarios = FuncionarioCache.todosFuncionarios;
        inputFuncionario.setItems(funcionarios);
        inputFuncionario.setConverter(new StringConverter<Funcionario>() {
            @Override
            public String toString(Funcionario funcionario) {
                return funcionario != null ? funcionario.getNome() + " - " + SetorCache.getSetorMapeado(funcionario.getIdSetor()) : "";
            }

            @Override
            public Funcionario fromString(String s) {
                for (Funcionario f : inputFuncionario.getItems()) {
                    String funcionario = f.getNome() + " - " + SetorCache.getSetorMapeado(f.getIdSetor());
                    if (funcionario.equals(s)) {
                        return f;
                    }
                }
                return null;
            }
        });
        inputTipoCertificado.setItems(certificados);
        inputTipoCertificado.setConverter(new StringConverter<TipoCertificado>() {
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
        inputFuncionario.setValue(funcionarios.getFirst());
        inputDataValidade.setValue(certificadoService.calcularValidade(inputDataEmissao.getValue(),inputTipoCertificado.getValue()));
    }

    public void validadeAlteracao(ActionEvent event) {
        inputDataValidade.setValue(certificadoService.calcularValidade(inputDataEmissao.getValue(),
                inputTipoCertificado.getValue()));
    }


    public void handleSalvar(ActionEvent event) {
        Certificado certificado = Certificado.CertificadoBuilder.builder()
                .idTipoCertificado(inputTipoCertificado.getValue().getId())
                .idFuncionario(inputFuncionario.getValue().getId())
                .dataEmissao(inputDataEmissao.getValue())
                .dataValidade(certificadoService.calcularValidade(inputDataEmissao.getValue(),inputTipoCertificado.getValue()))
                .atualizadoPor(null)
                .build();
        certificadoService.cadastrarCertificado(certificado);
    }

    public void handleCancelar(ActionEvent event) {
    }
}
