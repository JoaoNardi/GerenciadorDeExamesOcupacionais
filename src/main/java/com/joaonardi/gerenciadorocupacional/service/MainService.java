package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.cache.ExameCache;
import com.joaonardi.gerenciadorocupacional.cache.FuncionarioCache;
import com.joaonardi.gerenciadorocupacional.cache.SetorCache;
import com.joaonardi.gerenciadorocupacional.cache.TipoExameCache;
import com.joaonardi.gerenciadorocupacional.model.*;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class MainService {
    private static final CondicaoService condicaoService = new CondicaoService();
    private static ObservableList<Condicao> listaCondicoes = condicaoService.listarTodasCondicoes();
    private TipoExameService tipoExameService = new TipoExameService();
    private FuncionarioService funcionarioService = new FuncionarioService();
    final ExameService exameService = new ExameService();
    ObservableList<Exame> listaExames = exameService.listarExames();
    private final SetorService setorService = new SetorService();

    public static void loadInicial() throws Exception {
        listaCondicoes = condicaoService.listarTodasCondicoes();
        ExameCache.carregarExamesVigentes();
        TipoExameCache.carregarTiposExames();
        FuncionarioCache.carregarFuncionarios(true);
        SetorCache.carregarSetores();
    }

    public String verificaStatusFuncionario2(Funcionario funcionario) {
        String setorFuncionario = setorService.getSetorMapeadoPorId(funcionario.getIdSetor());
        List<String> examesPendentes = new ArrayList<>();

        for (Condicao condicao : listaCondicoes) {
            if (condicao != null) {
                if (condicao.getParametro() != null && condicao.getParametro().equalsIgnoreCase(setorFuncionario)) {
                    String tipoExameCond = tipoExameService
                            .getTipoExameMapeadoPorId(condicao.getTipoExameId())
                            .getNome();
                    boolean temExame = false;
                    for (Exame exame : listaExames) {
                        String tipoExameExame = tipoExameService
                                .getTipoExameMapeadoPorId(exame.getIdTipoExame())
                                .getNome();

                        if (tipoExameExame.equalsIgnoreCase(tipoExameCond)
                                && exame.getIdFuncionario().equals(funcionario.getId())) {
                            temExame = true;
                            break;
                        }
                    }
                    if (!temExame) {
                        examesPendentes.add(tipoExameCond);
                    }
                }
                try {
                    boolean temExame = false;
                    int idadeCond = Integer.parseInt(condicao.getParametro());
                    int idadeFuncionario = funcionarioService.calcularIdade(funcionario.getDataNascimento());
                    if (compara(idadeFuncionario, condicao.getOperador(), idadeCond)) {
                        String tipoExameCond = tipoExameService.getTipoExameMapeadoPorId(condicao.getTipoExameId()).getNome();
                        for (Exame exame : listaExames) {
                            String tipoExameExame = tipoExameService.getTipoExameMapeadoPorId(exame.getIdTipoExame()).getNome();
                            if (tipoExameExame.equalsIgnoreCase(tipoExameCond) && exame.getIdFuncionario().equals(funcionario.getId()))
                                temExame = true;
                            break;
                        }
                        if (!temExame) {
                            examesPendentes.add(tipoExameCond);
                        }

                    }

                } catch (NumberFormatException e) {

                }

            }
        }

        if (!examesPendentes.isEmpty()) {
            return "Exames pendentes: " + String.join(", ", examesPendentes);
        }

        return "OK";
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

}
