package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.Setor;
import com.joaonardi.gerenciadorocupacional.service.FuncionarioService;
import com.joaonardi.gerenciadorocupacional.service.SetorService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.swing.*;
import java.util.List;


public class FuncionarioController {
    public TextField inputNome;
    public TextField inputCpf;
    public DatePicker inputDataNascimento;
    public DatePicker inputDataAdmissao;
    public ChoiceBox<Setor> inputSetor;
    public Button btnSalvar;
    public Button btnCancelar;
    public CheckBox inputAtivo;
    FuncionarioService funcionarioService = new FuncionarioService();
    SetorService setorService = new SetorService();
    private Funcionario funcionario;

    Janela janela = new Janela();

    @FXML
    public void initialize() {
        List<Setor> setores = setorService.listarSetores();
        inputSetor.getItems().addAll(setores);
    }

    public void setFuncionario(Funcionario funcionario) throws Exception {
        Setor setor = setorService.consultarSetorPorId(funcionario.getIdSetor());
        this.funcionario = funcionario;
        if (funcionario !=null){
            inputNome.setText(funcionario.getNome());
            inputCpf.setText(funcionario.getCpf());
            inputDataNascimento.setValue(funcionario.getDataNascimento());
            inputDataAdmissao.setValue(funcionario.getDataAdmissao());
            inputSetor.setValue(setor);
        }
    }

    @FXML
    public void handleSalvarFuncionario(ActionEvent event) {
        if (this.funcionario == null || this.funcionario.getId() == null){
        this.funcionario = Funcionario
                .FuncionarioBuilder.builder()
                .id(null)
                .nome(inputNome.getText())
                .cpf(inputCpf.getText())
                .dataNascimento(inputDataNascimento.getValue())
                .dataAdmissao(inputDataAdmissao.getValue())
                .idSetor(inputSetor.getValue().getId())
                .ativo(inputAtivo.isSelected()).build();
        } else {
            this.funcionario = Funcionario
                    .FuncionarioBuilder.builder()
                    .id(this.funcionario.getId())
                    .nome(inputNome.getText())
                    .cpf(inputCpf.getText())
                    .dataNascimento(inputDataNascimento.getValue())
                    .dataAdmissao(inputDataAdmissao.getValue())
                    .idSetor(inputSetor.getValue().getId())
                    .ativo(inputAtivo.isSelected()).build();
        }
        try {
            funcionarioService.cadastrarFuncionario(this.funcionario);
            JOptionPane.showMessageDialog(null,"Cadastro realizado com sucesso");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,e.getMessage());
        }
        janela.fecharJanela(btnSalvar);
    }

    public void handleCancelarFuncionario(ActionEvent event) {
        janela.fecharJanela(btnCancelar);

    }




}
