package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.model.RelatorioItem;
import com.joaonardi.gerenciadorocupacional.model.TipoDe;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import com.joaonardi.gerenciadorocupacional.service.FuncionarioService;
import com.joaonardi.gerenciadorocupacional.service.RelatorioService;
import com.joaonardi.gerenciadorocupacional.service.TipoCertificadoService;
import com.joaonardi.gerenciadorocupacional.service.TipoExameService;
import com.joaonardi.gerenciadorocupacional.util.ComboBoxCustom;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RelatoriosPorFuncionariosControlller {
    // Services
    private static final FuncionarioService funcionarioService = new FuncionarioService();
    final TipoCertificadoService tipoCertificadoService = new TipoCertificadoService();
    final TipoExameService tipoExameService = new TipoExameService();
    final RelatorioService relatorioService = new RelatorioService();
    TipoDe opcaoTodos = TipoExame.TipoExameBuilder.builder().nome("Todos").build();

    //Elementos JavaFX
    @FXML
    private ComboBoxCustom<Funcionario> inputFuncionario;
    public DatePicker inputDataInicial;
    public DatePicker inputDataFinal;
    public RadioButton inputPorEmissao;
    public RadioButton inputPorValidade;
    public RadioButton inputExame;
    public RadioButton inputCertificado;
    public ComboBoxCustom<TipoDe> inputTipoDe;
    public Button btnGerarRelatorio;
    public Button btnImprimir;
    public Button btnSalvarPDF;
    public Button btnSalvarPlanilha;
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

    final Janela janela = new Janela();
    private boolean isListarExames = true;
    private boolean isListarCertifiados = true;

    //Dados a serem carregados
    private final ObservableList<TipoDe> tiposDeLista = FXCollections.observableArrayList();
    private ObservableList<RelatorioItem> relatorioLista = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        loadAll();
        setInputs();
        setIcones();
        disableButtons(true);
        tabelaVencimentos.setItems(null);
        setBindings();
    }

    private void setBindings() {
        ToggleGroup grupoTipoData = new ToggleGroup();
        inputPorEmissao.setToggleGroup(grupoTipoData);
        inputPorValidade.setToggleGroup(grupoTipoData);

        BooleanBinding inputsValidos =
                inputFuncionario.valueProperty().isNotNull()
                        .and(inputDataInicial.valueProperty().isNotNull())
                        .and(inputDataFinal.valueProperty().isNotNull())
                        .and(inputTipoDe.valueProperty().isNotNull())
                        .and(grupoTipoData.selectedToggleProperty().isNotNull())
                        .and(inputExame.selectedProperty()
                                .or(inputCertificado.selectedProperty()));
        btnGerarRelatorio.disableProperty().bind(inputsValidos.not());

        InvalidationListener listenerResetTabela = obs -> resetTabela();
        inputFuncionario.valueProperty().addListener(listenerResetTabela);
        inputDataInicial.valueProperty().addListener(listenerResetTabela);
        inputDataFinal.valueProperty().addListener(listenerResetTabela);
        inputTipoDe.valueProperty().addListener(listenerResetTabela);
        grupoTipoData.selectedToggleProperty().addListener(listenerResetTabela);
        inputExame.selectedProperty().addListener(listenerResetTabela);
        inputCertificado.selectedProperty().addListener(listenerResetTabela);
    }

    private void resetTabela() {
        disableButtons(true);
        tabelaVencimentos.setItems(null);
        tabelaVencimentos.refresh();
    }

    private void loadAll() {
        loadFuncionarios();
    }

    private void loadFuncionarios() {
        funcionarioService.carregarTodosFuncionarios();
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
        tiposDeLista.add(opcaoTodos);
        if (!inputExame.isSelected() || !inputCertificado.isSelected()) {
            if (isListarExames) {
                tipoExameService.carregarTipoExames();
                tiposDeLista.addAll(tipoExameService.listarTiposExame());
            } else if (isListarCertifiados) {
                tipoCertificadoService.carregarTiposCertificado();
                tiposDeLista.addAll(tipoCertificadoService.listarTiposCertificados());
            }
        }
    }

    private void setInputs() {
        inputDataInicial.setValue(LocalDate.now().minusMonths(12));
        inputDataFinal.setValue(LocalDate.now());
        inputFuncionario.setItemsAndDisplay(funcionarioService.listarFuncionarios(), List.of(Funcionario::getNome, f -> f.getSetor().getArea()));
        inputTipoDe.setItemsAndDisplay(tiposDeLista, List.of(TipoDe::getNome));
    }

    public void setTabelaVencimentos() {
        try {
            colunaId.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue().getId()));
            colunaTipo.setCellValueFactory(f -> {
                String tipo = f.getValue().getOrigem();
                return new SimpleStringProperty(tipo);
            });
            colunaDescricaoVencimentos.setCellValueFactory(f -> {
                String tipo = f.getValue().getOrigem();
                String descricao;
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
                String status;
                if (f.getValue().getAtualizadoPor() == null) {
                    status = "Vigente";
                } else {
                    status = "Prescrito";
                }
                return new SimpleStringProperty(status);
            });
            colunaAtualizadoPor.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue().getAtualizadoPor()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void handlePorEmissao() {
        inputPorEmissao.setSelected(true);
        inputPorValidade.setSelected(false);
        tipoData = "Validadade";
    }

    public void handlePorValidade() {
        inputPorEmissao.setSelected(false);
        inputPorValidade.setSelected(true);
        tipoData = "Emissao";
    }

    public void handleInputExame() {
        if (isListarExames) {
            inputExame.setSelected(false);
            isListarExames = false;
        } else {
            inputExame.setSelected(true);
            isListarExames = true;
        }
    }

    public void handleInputCertificado() {
        if (isListarCertifiados) {
            inputCertificado.setSelected(false);
            isListarCertifiados = false;
        } else {
            inputCertificado.setSelected(true);
            isListarCertifiados = true;
        }
    }

    public void handleGerarRelatorio() {
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
        disableButtons(false);
        relatorioService.carregarRelatorio(inputFuncionario.getValue(), inputData, inputDataInicial.getValue(),
                inputDataFinal.getValue(),
                inputTipoDe.getValue(), inputExame.isSelected(), inputCertificado.isSelected());
        relatorioLista = relatorioService.listarRelatorio();
        tabelaVencimentos.setItems(relatorioLista);
        setTabelaVencimentos();
    }

    public void handleSalvarPDF() {
        relatorioService.gerarPDF(janela.stage, relatorioLista, inputFuncionario.getValue(), tabelaVencimentos, inputDataInicial.getValue(),
                inputDataFinal.getValue(), inputExame.isSelected(), inputCertificado.isSelected(), inputTipoDe.getValue());
    }

    public void handleImprimir() {
        relatorioService.imprimir(relatorioLista, inputFuncionario.getValue(), tabelaVencimentos, inputDataInicial.getValue(),
                inputDataFinal.getValue(), inputExame.isSelected(), inputCertificado.isSelected(), inputTipoDe.getValue());
    }

    public void handleSalvarExcel() {
        relatorioService.gerarExcel(janela.stage, relatorioLista, inputFuncionario.getValue(), tabelaVencimentos, inputDataInicial.getValue(),
                inputDataFinal.getValue(), inputExame.isSelected(), inputCertificado.isSelected(), inputTipoDe.getValue());
    }
}
