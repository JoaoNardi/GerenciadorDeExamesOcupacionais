package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.ExameDAO;
import com.joaonardi.gerenciadorocupacional.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class ExameService {
    private final ExameDAO exameDAO = new ExameDAO();
    private final FuncionarioService funcionarioService = new FuncionarioService();
    private final ConjuntoService conjuntoService = new ConjuntoService();
    private final CondicaoService condicaoService = new CondicaoService();
    private static ObservableList<Exame> examesList = FXCollections.observableArrayList();

    public void carregarExamesVigentes() {
        examesList = exameDAO.listarExamesVigentes(true);
    }

    public ObservableList<Exame> listarExamesVingentes() {
        return examesList;
    }

    public Exame lancarExame(Exame exame) {
        Exame exameCadastrado = exameDAO.cadastrarExame(exame);
        carregarExamesVigentes();
        return exameCadastrado;
    }

    public void editarExame(Exame exame) {
        exameDAO.alterarExame(exame.getId(), exame);
        carregarExamesVigentes();
    }

    public LocalDate calcularValidadeExame(Funcionario funcionario, LocalDate emissaoExame, TipoExame tipoExame) {
        conjuntoService.carregarConjuntoTipoExameId(tipoExame.getId());
        ObservableList<Conjunto> conjuntos = conjuntoService.listarConjuntos();

        Integer periodicidade = calcularPeriodicidade(funcionario, tipoExame, conjuntos);

        if (periodicidade == null) {
            return null;
        }
        LocalDate dataValidade;

        dataValidade = emissaoExame.plusMonths(periodicidade);
        return dataValidade;
    }

    public Integer calcularPeriodicidade(Funcionario funcionario, TipoExame tipoExame, ObservableList<Conjunto> conjuntos) {

        // Carrega condições A
        // Carrega condições B
        Conjunto melhor = conjuntos.stream()
                .filter(conjunto -> {
                    condicaoService.carregarCondicoesPorConjuntoId(conjunto.getId());
                    return condicaoService.listarCondicoes().stream()
                            .allMatch(cond -> verificaCondicao(funcionario, cond));
                }).min((a, b) -> {
                    // Carrega condições A
                    condicaoService.carregarCondicoesPorConjuntoId(a.getId());
                    int sizeA = condicaoService.listarCondicoes().size();

                    // Carrega condições B
                    condicaoService.carregarCondicoesPorConjuntoId(b.getId());
                    int sizeB = condicaoService.listarCondicoes().size();
                    int result = Integer.compare(sizeB, sizeA);
                    if (result != 0) return result;
                    return Integer.compare(a.getPeriodicidade(), b.getPeriodicidade());
                })
                .orElse(null);
        return melhor != null ? melhor.getPeriodicidade() : null;
    }


    private boolean verificaCondicao(Funcionario funcionario, Condicao condicao) {
        String referencia = condicao.getReferencia();
        String operador = condicao.getOperador();
        String parametro = condicao.getParametro();

        switch (referencia.toLowerCase()) {
            case "idade":
                int idade = funcionarioService.calcularIdade(funcionario.getDataNascimento());
                int valor = Integer.parseInt(parametro);
                return compara(idade, operador, valor);

            case "setor":
                return comparaString(funcionario.getSetor().getArea(), operador, parametro);

//            case "enfermidade":
//                return
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

    private boolean comparaString(String valorFuncionario, String operador, String parametro) {
        return switch (operador) {
            case "==" -> valorFuncionario.equalsIgnoreCase(parametro);
            case "!=" -> !valorFuncionario.equalsIgnoreCase(parametro);
            case "<>" -> valorFuncionario.contains(parametro);
            case "><" -> !valorFuncionario.contains(parametro);
            default -> false;
        };
    }


    public String vencimentos(Tipo tipo) {
        if (tipo.getDataValidade() == null) {
            return "Sem Periodicidade";
        }
        int dias = (int) ChronoUnit.DAYS.between(LocalDate.now(), tipo.getDataValidade());
        String status = "Faltam: " + dias + " para o vencimento";
        if (dias < 0) {
            status = "Vencido " + dias + " de atraso";
        } else if (dias == 0) {
            status = "Vence Hoje";
        } else if (dias <= 7) {
            status = "Vence esta semana, em : " + dias + " dias ";
        } else if (dias <= 30) {
            status = "Vence dentro de um mês, em : " + dias + " dias ";
        } else if (dias <= 182) {
            status = "Vence neste semestre, em : " + dias + " dias ";
        }
        return status;
    }

    public ObservableList<Exame> listarExamePorVencimento(int diasVencimento) {
        LocalDate hoje = LocalDate.now();
        if (diasVencimento == 183) {
            return examesList;
        } else {
            List<Exame> list2 = examesList.stream()
                    .filter(f -> {
                        LocalDate validade = f.getDataValidade();
                        if (validade == null) {
                            return false;
                        }
                        int dias = (int) ChronoUnit.DAYS.between(hoje, validade);

                        if (diasVencimento == 0) {
                            return dias <= 0;
                        } else if (diasVencimento == 7) {
                            return dias > 0 && dias <= 7;
                        } else if (diasVencimento == 30) {
                            return dias > 7 && dias <= 30;
                        } else if (diasVencimento == 182) {
                            return dias > 30 && dias <= 182;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());

            return FXCollections.observableArrayList(list2);
        }
    }

    public void deletarExame(int id) {
        exameDAO.deletarExame(id);
        carregarExamesVigentes();
    }
}
