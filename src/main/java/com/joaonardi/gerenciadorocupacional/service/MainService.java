package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class MainService {

    private static final CondicaoService condicaoService = new CondicaoService();
    private static ObservableList<Condicao> listaCondicoes = condicaoService.listarTodasCondicoes();
    private TipoExameService tipoExameService = new TipoExameService();
    private static final FuncionarioService funcionarioService = new FuncionarioService();
    private static final ExameService exameService = new ExameService();
    public static ObservableList<Exame> listaExames = FXCollections.observableArrayList();
    private static final SetorService setorService = new SetorService();
    private TipoCertificadoService tipoCertificadoService = new TipoCertificadoService();
    private static final CertificadoService certificadoService = new CertificadoService();
    public static ObservableList<Certificado> listaCertificados = FXCollections.observableArrayList();


    public static void loadInicial() {
        funcionarioService.carregarFuncionarios(true);
        listaCondicoes = condicaoService.listarTodasCondicoes();
        listaExames = exameService.listarExamesVingentes();
        listaCertificados = certificadoService.listarCertificados();
        setorService.carregarSetores();
    }

    public Exame getExameVencido(Funcionario funcionario) {
        for (Exame exame : listaExames) {
            if (funcionario.getId() == exame.getIdFuncionario()) {
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
        for (Certificado certificado : listaCertificados) {
            if (funcionario.getId() == certificado.getIdFuncionario()) {
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

    public ObservableList<TipoDe> verificaStatusFuncionario(Funcionario funcionario) {
        ObservableList<TipoDe> pendecias = FXCollections.observableArrayList();

        for (Certificado certificado : listaCertificados) {
            if (funcionario.getId().equals(certificado.getIdFuncionario())) {
                int dias = (int) ChronoUnit.DAYS.between(LocalDate.now(), certificado.getDataValidade());
                if (dias <= 1) {
                    pendecias.add(tipoCertificadoService.getTipoCertificadoMapeadoPorId(certificado.getIdTipoCertificado()));
                }
            }
        }

        for (Exame exame : listaExames) {
            if (funcionario.getId().equals(exame.getIdFuncionario())) {
                int dias = (int) ChronoUnit.DAYS.between(LocalDate.now(), exame.getDataValidade());
                if (dias <= 1) {
                    pendecias.add(tipoExameService.getTipoExameMapeadoPorId(exame.getIdTipoExame()));

                }
            }
        }
        String setorFuncionario = setorService.getSetorMapeado(funcionario.getIdSetor());
        for (Condicao condicao : listaCondicoes) {
            if (condicao == null) continue;

            TipoExame tipoExameCond = null;
            if (condicao.getTipoExameId() != null) {
                var tipoExame = tipoExameService.getTipoExameMapeadoPorId(condicao.getTipoExameId());
                if (tipoExame != null) {
                    tipoExameCond = tipoExame;
                }
            }
            if (tipoExameCond == null) continue;

            boolean precisaFazerExame = false;
            if (condicao.getParametro() != null && condicao.getParametro().equalsIgnoreCase(setorFuncionario)) {
                precisaFazerExame = true;
            } else {
                try {
                    int idadeCond = Integer.parseInt(condicao.getParametro());
                    int idadeFuncionario = funcionarioService.calcularIdade(funcionario.getDataNascimento());
                    if (compara(idadeFuncionario, condicao.getOperador(), idadeCond)) {
                        precisaFazerExame = true;
                    }
                } catch (NumberFormatException ignored) {
                }
            }

            if (precisaFazerExame && !funcionarioJaTemExame(funcionario, tipoExameCond)) {
                if (!pendecias.contains(tipoExameCond)) {
                    pendecias.add(tipoExameCond);
                }
            }
        }
        if (pendecias.isEmpty()) {
            return null;
        }
        return pendecias;
    }

    private boolean funcionarioJaTemExame(Funcionario funcionario, TipoExame tipoExameCond) {
        for (Exame exame : listaExames) {
            TipoExame tipoExame = tipoExameService.getTipoExameMapeadoPorId(exame.getIdTipoExame());
            if (tipoExame != null &&
                    tipoExame.getNome().equalsIgnoreCase(tipoExameCond.getNome()) &&
                    exame.getIdFuncionario().equals(funcionario.getId())) {
                return true;
            }
        }
        return false;
    }


    private boolean compara(int valorFuncionario, String operador, int parametro) {
        return switch (operador) {
            case "==" -> valorFuncionario == parametro;
            case ">=" -> valorFuncionario >= parametro;
            case "<=" -> valorFuncionario <= parametro;
            case "!=" -> valorFuncionario != parametro;
            default -> false;
        };
    }

    public String descreveTipo(Exame f) {
        return tipoExameService.getTipoExameMapeadoPorId(f.getIdTipoExame()).getNome();
    }

    public String descreveTipo(Certificado f) {
        return tipoCertificadoService.getTipoCertificadoMapeadoPorId(f.getIdTipoCertificado()).getNome();
    }

}
