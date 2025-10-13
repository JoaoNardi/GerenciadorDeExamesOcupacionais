package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.RelatorioItem;
import com.joaonardi.gerenciadorocupacional.model.TipoDe;
import com.joaonardi.gerenciadorocupacional.service.*;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.controlsfx.control.Notifications;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RelatoriosPorFuncionariosControlller {
    // Services
    private static final FuncionarioService funcionarioService = new FuncionarioService();
    private static final SetorService setorService = new SetorService();
    TipoCertificadoService tipoCertificadoService = new TipoCertificadoService();
    TipoExameService tipoExameService = new TipoExameService();
    RelatorioService relatorioService = new RelatorioService();

    //Elementos JavaFX
    @FXML
    private ComboBox<Funcionario> inputFuncionario;
    public DatePicker inputDataInicial;
    public DatePicker inputDataFinal;
    public RadioButton inputPorEmissao;
    public RadioButton inputPorValidade;
    public String tipoData = "Emissao";
    //Tabela 1
    public TableView<RelatorioItem> tabelaVencimentos;
    public TableColumn<RelatorioItem, Integer> colunaId;
    public TableColumn<RelatorioItem, String> colunaTipo;
    public TableColumn<RelatorioItem, String> colunaDescricaoVencimentos;
    public TableColumn<RelatorioItem, LocalDate> colunaEmissaoVencimentos;
    public TableColumn<RelatorioItem, LocalDate> colunaValidadeVencimentos;
    public TableColumn<RelatorioItem, String> colunaStatus;
    public TableColumn<RelatorioItem, Integer> colunaAtualizadoPor;

    public RadioButton inputExame;
    public RadioButton inputCertificado;
    public ChoiceBox<TipoDe> inputTipoDe;
    public Button btnImprimir;
    public Button btnSalvarPDF;
    public Button btnSalvarPlanilha;

    Janela janela = new Janela();
    private boolean isListarExames = true;
    private boolean isListarCertifiados = true;

    //Dados a serem carregados
    private ObservableList<Funcionario> funcionariosLista = FXCollections.observableArrayList();
    private ObservableList<TipoDe> tiposDeLista = FXCollections.observableArrayList();
    private ObservableList<RelatorioItem> relatorioLista = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        loadAll();
        setInputs();
        setIcones();
        disableButtons(true);
        tabelaVencimentos.setItems(null);
    }

    private void loadAll() {
        loadFuncionarios();
    }

    private void loadFuncionarios() {
        funcionarioService.carregarTodosFuncionarios();
        funcionariosLista = funcionarioService.listarFuncionarios();
    }

    private void setIcones() {
        FontIcon iconeImprimir = new FontIcon(FontAwesomeSolid.PRINT);
        btnImprimir.setGraphic(iconeImprimir);
        FontIcon iconePDF = new FontIcon(FontAwesomeSolid.FILE_PDF);
        btnSalvarPDF.setGraphic(iconePDF);
        FontIcon iconeExcel = new FontIcon(FontAwesomeSolid.FILE_EXCEL);
        btnSalvarPlanilha.setGraphic(iconeExcel);
    }

    private void disableButtons(boolean able) {
        btnImprimir.setDisable(able);
        btnSalvarPDF.setDisable(able);
        btnSalvarPlanilha.setDisable(able);
    }

    public void handleCarregarTiposDe() {
        tiposDeLista.clear();
        tiposDeLista.add(null);
        if (!inputExame.isSelected() || !inputCertificado.isSelected()) {
            if (isListarExames) {
                tipoExameService.carregarTipoExames();
                tiposDeLista.addAll(tipoExameService.listarTiposExame());
            } else
            if (isListarCertifiados) {
                tipoCertificadoService.carregarTiposCertificado();
                tiposDeLista.addAll(tipoCertificadoService.listarTiposCertificados());
            }
        }
    }

    private void setInputs() {
        inputDataInicial.setValue(LocalDate.now().minusMonths(12));
        inputDataFinal.setValue(LocalDate.now());
        inputFuncionario.setItems(funcionariosLista);
        inputFuncionario.setValue(funcionariosLista.getFirst());
        inputFuncionario.setConverter(new StringConverter<Funcionario>() {
            @Override
            public String toString(Funcionario funcionario) {
                return funcionario != null ? funcionario.getNome() + " - " + setorService.getSetorMapeado(funcionario.getIdSetor()) : "";
            }

            @Override
            public Funcionario fromString(String s) {
                for (Funcionario f : inputFuncionario.getItems()) {
                    String funcionario = f.getNome() + " - " + setorService.getSetorMapeado(f.getIdSetor());
                    if (funcionario.equals(s)) {
                        return f;
                    }
                }
                return null;
            }
        });
        inputTipoDe.setItems(tiposDeLista);
        inputTipoDe.setConverter(new StringConverter<TipoDe>() {
            @Override
            public String toString(TipoDe tipoDe) {
                return tipoDe != null ? tipoDe.getNome() : "Todos";
            }

            @Override
            public TipoDe fromString(String s) {
                if (s == null) return null;
                for (TipoDe t : inputTipoDe.getItems()) {
                    if (s.equals(t.getNome())) {
                        return t;
                    }
                }
                return null;
            }
        });
    }

    public void setTabelaVencimentos() {
        try {
            colunaId.setCellValueFactory(f -> {
                return new SimpleObjectProperty<>(f.getValue().getId());
            });
            colunaTipo.setCellValueFactory(f -> {
                String tipo = f.getValue().getOrigem();
                return new SimpleStringProperty(tipo);
            });
            colunaDescricaoVencimentos.setCellValueFactory(f -> {
                String tipo = f.getValue().getOrigem();
                String descricao = "";
                if (tipo.equalsIgnoreCase("Exame")) {
                    descricao = tipoExameService.getTipoExameMapeadoPorId(f.getValue().getTipoId()).getNome();
                } else {
                    descricao = tipoCertificadoService.getTipoCertificadoMapeadoPorId(f.getValue().getTipoId()).getNome();
                }
                return new SimpleStringProperty(descricao);
            });
            colunaEmissaoVencimentos.setCellValueFactory(f -> {
                LocalDate emissao = f.getValue().getDataEmissao();
                return new SimpleObjectProperty<>(emissao);
            });
            colunaEmissaoVencimentos.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : DateTimeFormatter.ofPattern("dd/MM/yyyy").format(item));
                }
            });
            colunaValidadeVencimentos.setCellValueFactory(f -> {
                LocalDate validade = f.getValue().getDataValidade();
                return new SimpleObjectProperty<>(validade);
            });
            colunaValidadeVencimentos.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : DateTimeFormatter.ofPattern("dd/MM/yyyy").format(item));
                }
            });
            colunaStatus.setCellValueFactory(f -> {
                String status = "";
                if (f.getValue().getAtualizadoPor() == null) {
                    status = "Vigente";
                } else {
                    status = "Prescrito";
                }
                return new SimpleStringProperty(status);
            });
            colunaAtualizadoPor.setCellValueFactory(f -> {
                return new SimpleObjectProperty<>(f.getValue().getAtualizadoPor());
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void handlePorEmissao(ActionEvent event) {
        inputPorEmissao.setSelected(true);
        inputPorValidade.setSelected(false);
        tipoData = "Validadade";
    }

    public void handlePorValidade(ActionEvent event) {
        inputPorEmissao.setSelected(false);
        inputPorValidade.setSelected(true);
        tipoData = "Emissao";
    }

    public void handleInputExame(ActionEvent event) {
        if (isListarExames) {
            inputExame.setSelected(false);
            isListarExames = false;
        } else {
            inputExame.setSelected(true);
            isListarExames = true;
        }
    }

    public void handleInputCertificado(ActionEvent event) {
        if (isListarCertifiados) {
            inputCertificado.setSelected(false);
            isListarCertifiados = false;
        } else {
            inputCertificado.setSelected(true);
            isListarCertifiados = true;
        }
    }

    public void handleGerarRelatorio(ActionEvent event) {
        String inputData = "";
        if (inputExame.isSelected() && inputCertificado.isSelected()) {
            colunaId.setVisible(false);
            colunaAtualizadoPor.setVisible(false);
        } else {
            colunaId.setVisible(true);
            colunaAtualizadoPor.setVisible(true);
        }
        if (inputPorEmissao.isSelected()) {
            inputData = "Emissao";
        }
        if (inputPorValidade.isSelected()) {
            inputData = "Validade";
        }
        if (inputDataFinal.getValue().isBefore(inputDataInicial.getValue())) {
            Notifications.create().title("Erro")
                    .text("Período de datas inválido")
                    .hideAfter(Duration.seconds(3))
                    .owner(janela.stage)
                    .showError();
            initialize();
            return;
        }
        btnSalvarPDF.setDisable(false);
        btnImprimir.setDisable(false);
        btnSalvarPlanilha.setDisable(false);
        relatorioService.carregarRelatorio(inputFuncionario.getValue(), inputData, inputDataInicial.getValue(),
                inputDataFinal.getValue(),
                inputTipoDe.getValue(), inputExame.isSelected(), inputCertificado.isSelected());
        relatorioLista = relatorioService.listarRelatorio();
        tabelaVencimentos.setItems(relatorioLista);
        setTabelaVencimentos();
    }

    public void handleSalvarPDF(ActionEvent event) {
        relatorioService.gerarPDF(janela.stage, relatorioLista, inputFuncionario.getValue(),tabelaVencimentos,inputDataInicial.getValue(),
                inputDataFinal.getValue(), inputExame.isSelected(), inputCertificado.isSelected(), inputTipoDe.getValue());
    }

    public void handleImprimir(ActionEvent event) {
        relatorioService.imprimir(janela.stage, relatorioLista, inputFuncionario.getValue(),tabelaVencimentos,inputDataInicial.getValue(),
                inputDataFinal.getValue(), inputExame.isSelected(), inputCertificado.isSelected(), inputTipoDe.getValue());
    }
    public void handleSalvarExcel(ActionEvent event) {
        relatorioService.gerarExcel(janela.stage, relatorioLista, inputFuncionario.getValue(),tabelaVencimentos,inputDataInicial.getValue(),
                inputDataFinal.getValue(), inputExame.isSelected(), inputCertificado.isSelected(), inputTipoDe.getValue());
    }
}
