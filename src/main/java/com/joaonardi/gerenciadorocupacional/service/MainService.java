package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.model.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class MainService {

    private final CondicaoService condicaoService = new CondicaoService();
    private final TipoExameService tipoExameService = new TipoExameService();
    private final FuncionarioService funcionarioService = new FuncionarioService();
    private final ExameService exameService = new ExameService();
    private final SetorService setorService = new SetorService();
    private final TipoCertificadoService tipoCertificadoService = new TipoCertificadoService();
    private final CertificadoService certificadoService = new CertificadoService();

    private final ConjuntoService conjuntoService = new ConjuntoService();

    public void loadInicial() {
        tipoCertificadoService.carregarTiposCertificado();
        setorService.carregarSetores();
        funcionarioService.carregarFuncionariosPorStatus(true);
        exameService.carregarExamesVigentes();
        certificadoService.carregarCertificadosVigentes();
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
