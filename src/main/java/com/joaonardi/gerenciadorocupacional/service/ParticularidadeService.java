package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.ParticularidadeDAO;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.Particularidade;
import com.joaonardi.gerenciadorocupacional.model.VinculoFuncionarioParticularidade;
import javafx.collections.ObservableList;

public class ParticularidadeService {
    ParticularidadeDAO particularidadeDAO = new ParticularidadeDAO();

    public void cadastrarParticularidade(Particularidade particularidade) {
        particularidadeDAO.cadastrarParticularidade(particularidade);
    }


    public ObservableList<Particularidade> listarTodasParticularidades() {
        return particularidadeDAO.listarTodasParticularidades();
    }
    public ObservableList<Particularidade> listarTodasParticularidades(boolean ativos) {
        return particularidadeDAO.listarTodasParticularidades();
    }

    public void vincularFuncionarioParticularidade(Funcionario funcionario, Particularidade particularidade, String motivo) {
        particularidadeDAO.vincularParticularidadeFuncionario(funcionario, particularidade, motivo);
    }

    public ObservableList<VinculoFuncionarioParticularidade> listarTodosVinculos() {
        return particularidadeDAO.listarVinculos();
    }

    public ObservableList<VinculoFuncionarioParticularidade> listarTodosVinculos(boolean ativos) {
        return particularidadeDAO.listarVinculos();
    }
    public void deletarParticularidade(Particularidade particularidade){
        particularidadeDAO.deletarParticularidade(particularidade);
    }

    public void desvincularParticularidadeFuncionario(VinculoFuncionarioParticularidade vp) {
        try {
            particularidadeDAO.desvincularParticularidadesFuncionario(vp.getParticularidade(), vp.getFuncionario());
            listarTodosVinculos().remove(vp);
        } catch (Exception e){
            throw new RuntimeException("Erro ao desnvincular");
        }
    }

}
