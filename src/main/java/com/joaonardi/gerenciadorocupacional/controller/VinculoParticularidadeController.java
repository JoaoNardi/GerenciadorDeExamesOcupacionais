package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.Particularidade;
import com.joaonardi.gerenciadorocupacional.service.FuncionarioService;
import com.joaonardi.gerenciadorocupacional.service.ParticularidadeService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import com.joaonardi.gerenciadorocupacional.util.StringConverterUtil;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.util.StringConverter;
import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class VinculoParticularidadeController extends Janela {


    private final ParticularidadeService particularidadeService = new ParticularidadeService();
    private final FuncionarioService funcionarioService = new FuncionarioService();
    public Button btnCancelar;
    public Button btnSalvar;
    public ChoiceBox<Particularidade> inputParticularidade;
    public ChoiceBox<Funcionario> inputFuncionario;
    public TextArea inputMotivo;

    public void initialize() {

        particularidadeService.carregarTodasParticularidades();
        List<Function<Particularidade, String>> functionsParticularidade = new ArrayList<>();
        inputParticularidade.getItems().setAll(particularidadeService.listarParticularidades());
        functionsParticularidade.add(Particularidade::getNome);
        inputParticularidade.setConverter(StringConverterUtil.choice(particularidadeService.listarParticularidades(),
                functionsParticularidade));

        funcionarioService.carregarFuncionariosPorStatus(true);
        List<Function<Funcionario, String>> functionsFuncionario = new ArrayList<>();
        functionsFuncionario.add(Funcionario::getNome);
        inputFuncionario.setConverter(StringConverterUtil.choice(funcionarioService.listarFuncionarios(),
                functionsFuncionario));
        inputFuncionario.getItems().setAll(funcionarioService.listarFuncionarios());
    }


    public void handleSalvarVinculo(ActionEvent event) {
        salvar("salvar",
                btnSalvar,
                () -> {
                    particularidadeService.vincularFuncionarioParticularidade(inputFuncionario.getValue(),
                            inputParticularidade.getValue(),
                            inputMotivo.getText());
                });
    }

    public void handleCancelarVinculo(ActionEvent event) {
        fecharJanela(btnCancelar);
    }
}
