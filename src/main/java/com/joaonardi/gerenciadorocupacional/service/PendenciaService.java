package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

public class PendenciaService {
    private final CertificadoService certificadoService = new CertificadoService();
    private final ExameService exameService = new ExameService();
    private final TipoExameService tipoExameService = new TipoExameService();
    private final ConjuntoService conjuntoService = new ConjuntoService();
    private final CondicaoService condicaoService = new CondicaoService();
    private final FuncionarioService funcionarioService = new FuncionarioService();
    public final ObservableList<Pendencia> pendencias = FXCollections.observableArrayList();

    public void varreduraPendencias() {
        pendencias.clear();
        funcionarioService.listarFuncionariosPorStatus(true);
        for (Funcionario funcionario : funcionarioService.listarFuncionariosPorStatus(true)) {

            for (Certificado certificado : certificadoService.listarCertificados()) {
                if (funcionario.getId().equals(certificado.getFuncionario().getId())) {
                    if (certificado.getDataValidade() != null){
                        int dias = (int) ChronoUnit.DAYS.between(LocalDate.now(), certificado.getDataValidade());
                        if (dias <= 1) {
                            criarPendencia(funcionario, certificado.getTipoCertificado());
                        }
                    }
                }
            }

            for (Exame exame : exameService.listarExamesVingentes()) {
                if (funcionario.getId().equals(exame.getFuncionario().getId())) {
                    int dias = (int) ChronoUnit.DAYS.between(LocalDate.now(), exame.getDataValidade());
                    if (dias <= 1) {
                        criarPendencia(funcionario, exame.getTipoExame());
                    }
                }
            }

            String setorFuncionario = funcionario.getSetor().getArea();

            for (TipoExame tipoExameL : tipoExameService.listarTiposExame()) {
                ObservableList<Conjunto> list = conjuntoService.listarConjuntos(tipoExameL.getId());
                for (Conjunto conjunto : list.stream().sorted(Comparator.comparing(Conjunto::getPeriodicidade)).toList()) {
                    condicaoService.carregarCondicoesPorConjuntoId(conjunto.getId());
                    List<Condicao> condicoes = condicaoService.listarCondicoes();

                    if (condicoes.isEmpty()) continue;

                    TipoExame tipoExameCond = conjunto.getTipoExame();
                    if (tipoExameCond == null) continue;

                    boolean atendeTodasCondicoes = true;

                    for (Condicao condicao : condicoes) {
                        if (condicao == null) {
                            atendeTodasCondicoes = false;
                            break;
                        }

                        boolean atendeEstaCondicao = false;

                        if (condicao.getParametro() != null && condicao.getParametro().equalsIgnoreCase(setorFuncionario)) {
                            atendeEstaCondicao = true;
                        } else {
                            try {
                                int idadeCond = Integer.parseInt(condicao.getParametro());
                                int idadeFuncionario = funcionarioService.calcularIdade(funcionario.getDataNascimento());
                                if (compara(idadeFuncionario, condicao.getOperador(), idadeCond)) {
                                    atendeEstaCondicao = true;
                                }
                            } catch (NumberFormatException ignored) {
                            }
                        }

                        if (!atendeEstaCondicao) {
                            atendeTodasCondicoes = false;
                            break;
                        }
                    }

                    if (atendeTodasCondicoes && !funcionarioJaTemExame(funcionario, tipoExameCond)) {
                        criarPendencia(funcionario, tipoExameCond);
                    }
                }
            }
        }
    }

    private boolean funcionarioJaTemExame(Funcionario funcionario, TipoExame tipoExameCond) {
        for (Exame exame : exameService.listarExamesVingentes()) {
            TipoExame tipoExame = exame.getTipoExame();
            if (tipoExame != null &&
                    tipoExame.getNome().equalsIgnoreCase(tipoExameCond.getNome()) &&
                    exame.getFuncionario().getId().equals(funcionario.getId())) {
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
    // tinhamu leleko tiamu jhonjhonaaaa !!!
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
        return nomes.isEmpty() ? "" : String.join(", ", nomes);
    }
}