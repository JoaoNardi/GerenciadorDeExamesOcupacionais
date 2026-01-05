package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.ParticularidadeDAO;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.Particularidade;
import com.joaonardi.gerenciadorocupacional.model.VinculoFuncionarioParticularidade;
import javafx.collections.ObservableList;

public class ParticularidadeService {
    private final ParticularidadeDAO particularidadeDAO = new ParticularidadeDAO();

    public void cadastrarParticularidade(Particularidade particularidade) {
        if (particularidade.getId() == null) {
            particularidadeDAO.cadastrarParticularidade(particularidade);
        }
        if (particularidade.getId() != null) {
            particularidadeDAO.atualizarParticularidade(particularidade.getId(), particularidade);
        }
    }

    public ObservableList<Particularidade> listarTodasParticularidades() {
        return particularidadeDAO.listarTodasParticularidades();
    }

    public ObservableList<Particularidade> listarTodasParticularidades(boolean ativos) {
        return particularidadeDAO.listarTodasParticularidades();
    }

    public void vincularFuncionarioParticularidade(VinculoFuncionarioParticularidade vinculoFuncionarioParticularidade) {
        System.out.println(vinculoFuncionarioParticularidade);
        if (vinculoFuncionarioParticularidade.getId() == null) {
            particularidadeDAO.vincularParticularidadeFuncionario(vinculoFuncionarioParticularidade);
        }
        if (vinculoFuncionarioParticularidade.getId() != null) {
            particularidadeDAO.atualizarMotivoVinculo(vinculoFuncionarioParticularidade);
        }
    }

    public ObservableList<VinculoFuncionarioParticularidade> listarTodosVinculos() {
        return particularidadeDAO.listarVinculos();
    }

    public ObservableList<VinculoFuncionarioParticularidade> listarTodosVinculos(boolean ativos) {
        return particularidadeDAO.listarVinculos();
    }

    public ObservableList<VinculoFuncionarioParticularidade> listarParticularidadesVinculadas(Funcionario funcionario) {
        return particularidadeDAO.listarParticularidadePorFuncionario(funcionario);
    }

    public void deletarParticularidade(Particularidade particularidade) {
        particularidadeDAO.deletarParticularidade(particularidade);
    }

    public void desvincularParticularidadeFuncionario(VinculoFuncionarioParticularidade vp) {
        try {
            particularidadeDAO.desvincularParticularidadesFuncionario(vp);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao desnvincular");
        }
    }

}
