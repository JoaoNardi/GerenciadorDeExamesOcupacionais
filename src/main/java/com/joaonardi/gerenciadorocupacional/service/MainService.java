package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.cache.ExameCache;
import com.joaonardi.gerenciadorocupacional.cache.FuncionarioCache;
import com.joaonardi.gerenciadorocupacional.cache.SetorCache;
import com.joaonardi.gerenciadorocupacional.cache.TipoExameCache;
import com.joaonardi.gerenciadorocupacional.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class MainService {
    private static final CondicaoService condicaoService = new CondicaoService();
    private static ObservableList<Condicao> listaCondicoes = condicaoService.listarTodasCondicoes();
    private TipoExameService tipoExameService = new TipoExameService();
    private FuncionarioService funcionarioService = new FuncionarioService();
    public static ObservableList<Exame> listaExames = FXCollections.observableArrayList();
    private final SetorService setorService = new SetorService();


    public static void loadInicial() throws Exception {
        listaCondicoes = condicaoService.listarTodasCondicoes();
        ExameCache.carregarExamesVigentes();
        listaExames = ExameCache.todosExames;
        TipoExameCache.carregarTiposExames();
        FuncionarioCache.carregarFuncionarios(true);
        SetorCache.carregarSetores();
    }

    public List<TipoExame> verificaStatusFuncionario2(Funcionario funcionario) {
        String setorFuncionario = setorService.getSetorMapeadoPorId(funcionario.getIdSetor());
        List<TipoExame> examesPendentes = new ArrayList<>();

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
            }
            else {
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
                examesPendentes.add(tipoExameCond);
            }
        }
        if (examesPendentes.isEmpty()){
            return null;
        }
        return examesPendentes;
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

}
