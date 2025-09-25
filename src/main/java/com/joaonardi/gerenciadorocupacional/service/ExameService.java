package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.cache.ExameCache;
import com.joaonardi.gerenciadorocupacional.cache.SetorCache;
import com.joaonardi.gerenciadorocupacional.dao.CondicaoDAO;
import com.joaonardi.gerenciadorocupacional.dao.ExameDAO;
import com.joaonardi.gerenciadorocupacional.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class ExameService {
    private ExameDAO exameDAO = new ExameDAO();
    private CondicaoDAO condicaoDAO = new CondicaoDAO();
    private final FuncionarioService funcionarioService = new FuncionarioService();

    public ObservableList<Exame> listarExamesVingentes(){
        return exameDAO.listarExamesVigentes(true);
    }

    public Exame lancarExame(Exame exame) {
        Exame exameCadastrado = exameDAO.cadastrarExame(exame);
        ExameCache.carregarExamesVigentes();
        return exameCadastrado;
    }

    public void editarExame(Exame exame) {
        exameDAO.alterarExame(exame.getId(), exame);
        ExameCache.carregarExamesVigentes();
    }

    public LocalDate calcularValidadeExame(Funcionario funcionario, LocalDate emissaoExame, TipoExame tipoExame) {
        ObservableList<Condicao> listaCondicao = condicaoDAO.listarCondicoesPorTipoExameId(tipoExame.getId());

        Integer periodicidade = calcularPeriodicidade(funcionario, tipoExame, listaCondicao);

        if (periodicidade== null){
            return null;
        }
        LocalDate dataValidade;
        if (tipoExame.getPeriodicidade().equals(0) && listaCondicao.isEmpty()) {
            return null;
        }
        dataValidade = emissaoExame.plusMonths(periodicidade);
        return dataValidade;
    }

    public Integer calcularPeriodicidade(Funcionario funcionario, TipoExame tipoExame, ObservableList<Condicao> listaCondicao) {
        int periodicidade = tipoExame.getPeriodicidade(); // periodicidade padrao
        for (Condicao condicao : listaCondicao) {
            if (verificaCondicao(funcionario, condicao)) {
                periodicidade = Math.min(periodicidade, condicao.getPeriodicidade()); // pegar a menor periodicidade em caso de conflito
            }
        }
        if (periodicidade == 0) {
            return null;
        }
        return periodicidade;
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
                return comparaString(SetorCache.getSetorMapeado(funcionario.getIdSetor()), operador, parametro);

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
        Integer dias = (int) ChronoUnit.DAYS.between(LocalDate.now(), tipo.getDataValidade());
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
        ObservableList<Exame> list = listarExamesVingentes();
        LocalDate hoje = LocalDate.now();
        if (diasVencimento == 183) {
            return list;
        } else {
            List<Exame> list2 = list.stream()
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
        ExameCache.carregarExamesVigentes();
    }
}
