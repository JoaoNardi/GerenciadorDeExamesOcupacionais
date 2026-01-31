package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.Setor;
import com.joaonardi.gerenciadorocupacional.model.VinculoFuncionarioParticularidade;
import com.joaonardi.gerenciadorocupacional.service.FuncionarioService;
import com.joaonardi.gerenciadorocupacional.service.ParticularidadeService;
import com.joaonardi.gerenciadorocupacional.service.SetorService;
import com.joaonardi.gerenciadorocupacional.util.*;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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
    public TableView<VinculoFuncionarioParticularidade> tabelaParticularidade;
    public TableColumn<VinculoFuncionarioParticularidade, String> colunaParticularidades;
    public TableColumn<VinculoFuncionarioParticularidade, String> colunaTipoExame;
    public TableColumn<VinculoFuncionarioParticularidade, String> colunaMotivo;
    public Button btnRemover;
    public Button btnEditar;

    private final ParticularidadeService particularidadeService = new ParticularidadeService();

    private Funcionario funcionario;

    final Janela<Funcionario> janela = new Janela<>();

    @FXML
    public void initialize() {
        setores.setAll(setorService.listarSetores());
        inputSetor.setItemsAndDisplay(setorService.listarSetores(), List.of(Setor::getArea));
        setBindings();
    }

    private void setTabelaParticularidade() {
        if (funcionario == null) {
            return;
        }
        colunaParticularidades.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getParticularidade().getNome()));
        colunaTipoExame.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getParticularidade().getTipoExame().getNome()));
        colunaMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        tabelaParticularidade.setItems(particularidadeService.listarParticularidadesVinculadas(funcionario, true));
    }

    private void setBindings() {
        BooleanBinding tableItemSelected = tabelaParticularidade.getSelectionModel().selectedItemProperty().isNull();
        btnRemover.disableProperty().bind(tableItemSelected);
        btnEditar.disableProperty().bind(tableItemSelected);

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
        Platform.runLater(()->{
            if (objeto != null) {
                this.funcionario = objeto;
                setTabelaParticularidade();
                inputNome.setText(objeto.getNome());
                inputCpf.setText(FormataCPF.outPutCPF(objeto.getCpf()));
                inputDataNascimento.setValue(objeto.getDataNascimento());
                inputDataAdmissao.setValue(objeto.getDataAdmissao());
                inputSetor.setValue(objeto.getSetor());
            }
        });
    }

    @FXML
    public void handleSalvarFuncionario() {
        String acao = "";
        if (this.funcionario == null || this.funcionario.getId() == null) {
            acao = "salvo";
            this.funcionario = Funcionario
                    .FuncionarioBuilder.builder()
                    .id(null)
                    .nome(inputNome.getText())
                    .cpf(FormataCPF.inputCPF(inputCpf.getText()))
                    .dataNascimento(inputDataNascimento.getValue())
                    .dataAdmissao(inputDataAdmissao.getValue())
                    .setor(inputSetor.getValue())
                    .ativo(inputAtivo.isSelected()).build();
        } else {
            acao = "atualizado";
            this.funcionario = Funcionario
                    .FuncionarioBuilder.builder()
                    .id(this.funcionario.getId())
                    .nome(inputNome.getText())
                    .cpf(FormataCPF.inputCPF(inputCpf.getText()))
                    .dataNascimento(inputDataNascimento.getValue())
                    .dataAdmissao(inputDataAdmissao.getValue())
                    .setor(inputSetor.getValue())
                    .ativo(inputAtivo.isSelected()).build();
        }
        salvar("Funcionário", acao, btnSalvar, () -> funcionarioService.cadastrarFuncionario(this.funcionario));
    }

    public void handleCancelarFuncionario() {
        janela.fecharJanela(btnCancelar);
    }

    public void handleAdicionarVinculo() {
        VinculoFuncionarioParticularidade vinculoFuncionarioParticularidade =
                VinculoFuncionarioParticularidade.VinculoFuncionarioParticularidadeBuilder.builder()
                        .funcionario(funcionario)
                        .build();
        new Janela<>().abrirJanela("/view/VinculoParticularidadesFuncionarios.fxml", "Vincular Particularidades", janela.stage,
                this::initialize, vinculoFuncionarioParticularidade);
    }

    public void handleRemoverParticularidade() {
        particularidadeService.ativarInativarVinculo(tabelaParticularidade.getSelectionModel().getSelectedItem());
        setTabelaParticularidade();
    }

    public void handleEditarVinculo() {
        new Janela<>().abrirJanela("/view/VinculoParticularidadesFuncionarios.fxml", "Vincular Particularidades", janela.stage,
                this::initialize, tabelaParticularidade.getSelectionModel().getSelectedItem());
    }
}
