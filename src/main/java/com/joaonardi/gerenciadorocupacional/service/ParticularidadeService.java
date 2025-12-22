package com.joaonardi.gerenciadorocupacional.service;

import com.joaonardi.gerenciadorocupacional.dao.ParticularidadeDAO;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.Particularidade;
import com.joaonardi.gerenciadorocupacional.model.VinculoFuncionarioParticularidade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ParticularidadeService {
    private ObservableList<Particularidade> listaParticularidades = FXCollections.observableArrayList();
    ParticularidadeDAO particularidadeDAO = new ParticularidadeDAO();

    public void cadastrarParticularidade(Particularidade particularidade) {
        particularidadeDAO.cadastrarParticularidade(particularidade);
    }

    public ObservableList<Particularidade> listarParticularidades() {
        return listaParticularidades;
    }

    public void vincularFuncionarioParticularidade(Funcionario funcionario, Particularidade particularidade, String motivo) {
        particularidadeDAO.vincularParticularidadeFuncionario(funcionario, particularidade, motivo);

    }

    public ObservableList<VinculoFuncionarioParticularidade> listarTodosVinculos() {

        return particularidadeDAO.listarVinculos();
    }

    public void desvincularParticularidadeFuncionario(VinculoFuncionarioParticularidade vp) {
        try {
            particularidadeDAO.desvincularParticularidadesFuncionario(vp.getParticularidade(), vp.getFuncionario());
            listarTodosVinculos().remove(vp);
        } catch (Exception e){
            throw new RuntimeException("Erro ao desnvincular");
        }
    }

    public void carregarTodasParticularidades() {
        listaParticularidades = particularidadeDAO.listarTodasParticularidades();
    }
}
