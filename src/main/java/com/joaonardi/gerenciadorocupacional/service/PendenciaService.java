package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PendenciaService {
    private final CertificadoService certificadoService = new CertificadoService();
    private final TipoCertificadoService tipoCertificadoService = new TipoCertificadoService();
    private final ExameService exameService = new ExameService();
    private final TipoExameService tipoExameService = new TipoExameService();
    private final SetorService setorService = new SetorService();
    private final ConjuntoService conjuntoService = new ConjuntoService();
    private final CondicaoService condicaoService = new CondicaoService();
    private final FuncionarioService funcionarioService = new FuncionarioService();
    public final ObservableList<Pendencia> pendencias = FXCollections.observableArrayList();

    public void varreduraPendencias() {
        pendencias.clear();
        funcionarioService.carregarFuncionariosPorStatus(true);
        tipoExameService.carregarTipoExames();
        setorService.carregarSetores();

        for (Funcionario funcionario : funcionarioService.listarFuncionarios()) {
            for (Certificado certificado : certificadoService.listarCertificados()) {
                if (funcionario.getId().equals(certificado.getIdFuncionario())) {
                    int dias = (int) ChronoUnit.DAYS.between(LocalDate.now(), certificado.getDataValidade());
                    if (dias <= 1) {
                        criarPendencia(funcionario, tipoCertificadoService.getTipoCertificadoMapeadoPorId(certificado.getIdTipoCertificado()));
                    }
                }
            }

            for (Exame exame : exameService.listarExamesVingentes()) {
                if (funcionario.getId().equals(exame.getIdFuncionario())) {
                    int dias = (int) ChronoUnit.DAYS.between(LocalDate.now(), exame.getDataValidade());
                    if (dias <= 1) {
                        criarPendencia(funcionario, tipoExameService.getTipoExameMapeadoPorId(exame.getIdTipoExame()));
                    }
                }
            }

            String setorFuncionario = setorService.getSetorMapeado(funcionario.getIdSetor());
            for (TipoExame tipoExameL : tipoExameService.listarTiposExame()) {
                conjuntoService.carregarConjuntoTipoExameId(tipoExameL.getId());
                for (Conjunto conjunto : conjuntoService.listarConjuntos()) {
                    condicaoService.carregarCondicoesPorConjuntoId(conjunto.getId());
                    for (Condicao condicao : condicaoService.listarCondicoes()) {
                        if (condicao == null) continue;
                        TipoExame tipoExameCond = null;
                        if (conjunto.getTipoExameId() != null) {
                            TipoExame tipoExame = tipoExameService.getTipoExameMapeadoPorId(conjunto.getTipoExameId());
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
                            criarPendencia(funcionario, tipoExameCond);
                        }
                    }
                }
            }
        }
    }

    private boolean funcionarioJaTemExame(Funcionario funcionario, TipoExame tipoExameCond) {
        for (Exame exame : exameService.listarExamesVingentes()) {
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

    private void criarPendencia(Funcionario funcionario, TipoDe tipoDe) {
        Pendencia pendencia = new Pendencia(funcionario, tipoDe);
        if (!pendencias.contains(pendencia)) {
            pendencias.add(pendencia);
        }
    }

    public Pendencia getPendenciaFuncionario(Funcionario f) {
        return pendencias.stream()
                .filter(p -> p.funcionario().getId().equals(f.getId()))
                .toList().getFirst();
    }

    public String listaPendenciasPorFuncionario(Funcionario f) {
        var nomes = pendencias.stream()
                .filter(p -> p.funcionario().getId().equals(f.getId()))
                .map(p -> p.tipoDe().getNome())
                .toList();
        return nomes.isEmpty() ? "ok" : String.join(", ", nomes);
    }
}