package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.ParticularidadeDAO;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.Particularidade;
import com.joaonardi.gerenciadorocupacional.model.VinculoFuncionarioParticularidade;
import javafx.collections.ObservableList;

import java.time.LocalDate;

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
        if (vinculoFuncionarioParticularidade.getId() == null) {
            particularidadeDAO.vincularParticularidadeFuncionario(vinculoFuncionarioParticularidade);
        }
        if (vinculoFuncionarioParticularidade.getId() != null) {
            particularidadeDAO.atualizarVinculoFuncionarioParticularidade(vinculoFuncionarioParticularidade);
        }
    }

    public ObservableList<VinculoFuncionarioParticularidade> listarTodosVinculos(boolean ativos) {
        return particularidadeDAO.listarVinculos(ativos);
    }

    public ObservableList<VinculoFuncionarioParticularidade> listarParticularidadesVinculadas(Funcionario funcionario, boolean inVigentes) {
        return particularidadeDAO.listarParticularidadePorFuncionario(funcionario, inVigentes);
    }

    public void ativarInativarVinculo(VinculoFuncionarioParticularidade vfp){
        VinculoFuncionarioParticularidade vfp1 = null;
        if (vfp.getDataExclusao() == null){
             vfp1 = VinculoFuncionarioParticularidade
                     .VinculoFuncionarioParticularidadeBuilder.builder()
                            .id(vfp.getId())
                            .dataExclusao(LocalDate.now())
                            .build();
        }
        if (vfp.getDataExclusao() != null){
            vfp1 = VinculoFuncionarioParticularidade
                    .VinculoFuncionarioParticularidadeBuilder.builder()
                    .id(vfp.getId())
                    .dataExclusao(null)
                    .build();
        }
        particularidadeDAO.AtivarInativarVinculoParticularidadeFuncionario(vfp1);
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
