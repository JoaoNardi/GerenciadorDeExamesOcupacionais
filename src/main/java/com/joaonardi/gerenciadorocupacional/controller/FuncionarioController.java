package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.Setor;
import com.joaonardi.gerenciadorocupacional.service.FuncionarioService;
import com.joaonardi.gerenciadorocupacional.service.SetorService;
import com.joaonardi.gerenciadorocupacional.util.ComboBoxCustom;
import com.joaonardi.gerenciadorocupacional.util.DatePickerCustom;
import com.joaonardi.gerenciadorocupacional.util.Editavel;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.util.List;


public class FuncionarioController extends Janela<Funcionario> implements Editavel<Funcionario> {
    public TextField inputNome;
    public TextField inputCpf;
    public DatePickerCustom inputDataNascimento;
    public DatePickerCustom inputDataAdmissao;
    public ComboBoxCustom<Setor> inputSetor;
    public Button btnSalvar;
    public Button btnCancelar;
    public CheckBox inputAtivo;
    final FuncionarioService funcionarioService = new FuncionarioService();
    final SetorService setorService = new SetorService();
    private final ObservableList<Setor> setores = FXCollections.observableArrayList();


    private Funcionario funcionario;

    final Janela janela = new Janela();

    @FXML
    public void initialize() {
        setores.setAll(setorService.listarSetores());
        inputSetor.setItemsAndDisplay(setorService.listarSetores(), List.of(Setor::getArea));
        setBindings();

    }

    private void setBindings(){
        BooleanBinding inputsValidos =
                inputNome.textProperty().isNotNull()
                        .and(inputCpf.textProperty().isNotNull())
                        .and(inputDataNascimento.valueProperty().isNotNull())
                        .and(inputDataAdmissao.valueProperty().isNotNull())
                        .and(inputSetor.valueProperty().isNotNull());
        btnSalvar.disableProperty().bind(inputsValidos.not());
    }

    @Override
    public void set(Funcionario objeto) {
        super.set(objeto);
        if (objeto != null) {
            inputNome.setText(objeto.getNome());
            inputCpf.setText(objeto.getCpf());
            inputDataNascimento.setValue(objeto.getDataNascimento());
            inputDataAdmissao.setValue(objeto.getDataAdmissao());
            inputSetor.setValue(objeto.getSetor());
        }
    }

    @FXML
    public void handleSalvarFuncionario() {
        if (this.funcionario == null || this.funcionario.getId() == null) {
            this.funcionario = Funcionario
                    .FuncionarioBuilder.builder()
                    .id(null)
                    .nome(inputNome.getText())
                    .cpf(inputCpf.getText())
                    .dataNascimento(inputDataNascimento.getValue())
                    .dataAdmissao(inputDataAdmissao.getValue())
                    .setor(inputSetor.getValue())
                    .ativo(inputAtivo.isSelected()).build();
        } else {
            this.funcionario = Funcionario
                    .FuncionarioBuilder.builder()
                    .id(this.funcionario.getId())
                    .nome(inputNome.getText())
                    .cpf(inputCpf.getText())
                    .dataNascimento(inputDataNascimento.getValue())
                    .dataAdmissao(inputDataAdmissao.getValue())
                    .setor(inputSetor.getValue())
                    .ativo(inputAtivo.isSelected()).build();
        }
        try {
            funcionarioService.cadastrarFuncionario(this.funcionario);
            JOptionPane.showMessageDialog(null,"Cadastro realizado com sucesso");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,e.getMessage());
            return;
        }
        janela.fecharJanela(btnSalvar);
    }

    public void handleCancelarFuncionario() {
        janela.fecharJanela(btnCancelar);

    }

    public void handleAdicionarVincularidade() {
    }

    public void handleRemoverParticularidade() {
    }
}
