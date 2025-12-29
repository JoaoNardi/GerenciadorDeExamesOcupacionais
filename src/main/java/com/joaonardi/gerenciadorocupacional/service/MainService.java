package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.model.Certificado;
import com.joaonardi.gerenciadorocupacional.model.Exame;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.Setor;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class MainService {

    private final FuncionarioService funcionarioService = new FuncionarioService();
    private final ExameService exameService = new ExameService();
    private final SetorService setorService = new SetorService();
    private final TipoCertificadoService tipoCertificadoService = new TipoCertificadoService();
    private final CertificadoService certificadoService = new CertificadoService();
    private final ObservableList<Setor> setores = FXCollections.observableArrayList();


    public void loadInicial() {
        tipoCertificadoService.carregarTiposCertificado();
        funcionarioService.carregarFuncionariosPorStatus(true);
        exameService.carregarExamesVigentes();
        certificadoService.carregarCertificadosVigentes();
        setListenners();
    }

    private void setListenners() {

    }

    public Exame getExameVencido(Funcionario funcionario) {
        for (Exame exame : exameService.listarExamesVingentes()) {
            if (Objects.equals(funcionario.getId(), exame.getFuncionario().getId())) {
                int dias = (int) ChronoUnit.DAYS.between(LocalDate.now(), exame.getDataValidade());
                if (dias <= 1) {
                    return exame;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    public Certificado getCetificadoVencido(Funcionario funcionario) {
        for (Certificado certificado : certificadoService.listarCertificados()) {
            if (Objects.equals(funcionario.getId(), certificado.getFuncionario().getId())) {
                int dias = (int) ChronoUnit.DAYS.between(LocalDate.now(), certificado.getDataValidade());
                if (dias <= 1) {
                    return certificado;
                } else {
                    return null;
                }
            }
        }
        return null;
    }


    public String descreveTipo(Exame f) {
        return f.getTipoExame().getNome();
    }

    public String descreveTipo(Certificado f) {
        return f.getTipoCertificado().getNome();
    }

}
