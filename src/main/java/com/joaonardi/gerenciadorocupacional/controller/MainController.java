package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.MainApp;
import com.joaonardi.gerenciadorocupacional.model.*;
import com.joaonardi.gerenciadorocupacional.service.*;
import com.joaonardi.gerenciadorocupacional.util.Coluna;
import com.joaonardi.gerenciadorocupacional.util.FormataData;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.PopOver;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class MainController {
    ExamesController examesController = new ExamesController();
    CertificadoController certificadoController = new CertificadoController();
    FuncionarioController funcionarioController = new FuncionarioController();

    //tabela Principal Geral
    public TableView<Funcionario> tabelaPrincipal;
    public TableColumn<Funcionario, String> colunaFuncionarioGeral;
    public TableColumn<Funcionario, String> colunaIdadeGeral;
    public TableColumn<Funcionario, String> colunaSetorGeral;
    public TableColumn<Funcionario, String> colunaAniversario;
    public TableColumn<Funcionario, String> colunaStatusGeral;
    public TableColumn<Funcionario, Node> colunaAcoesGeral;
    //botoes
    public Button btnVencidos;
    public Button btnVencemSemana;
    public Button btnVencemMes;
    public Button btnVencemSemestre;
    public Button btnTodos;
    public Label labelTodos;
    public Label labelVencemMes;
    public Label labelVencemSemana;
    public Label labelVencemSemestre;
    public Label labelVencidos;
    //tabela Secundaria
    public TableView<Tipo> tabelaVencimentos;
    public TableColumn<Tipo, String> colunaFuncionarioVencimentos;
    public TableColumn<Tipo, String> colunaIdadeVencimentos;
    public TableColumn<Tipo, String> colunaSetorVencimentos;
    public TableColumn<Tipo, String> colunaTipoVencimentos;
    public TableColumn<Tipo, String> colunaDescricaoVencimentos;
    public TableColumn<Tipo, LocalDate> colunaEmissaoVencimentos;
    public TableColumn<Tipo, LocalDate> colunaValidadeVencimentos;
    public TableColumn<Tipo, String> colunaStatusVencimentos;
    public TableColumn<Tipo, Node> colunaAcoesVencimentos;


    final Janela<?> janela = new Janela<>();

    final TipoCertificadoService tipoCertificadoService = new TipoCertificadoService();
    final FuncionarioService funcionarioService = new FuncionarioService();
    final ExameService exameService = new ExameService();
    final MainService mainService = new MainService();
    final SetorService setorService = new SetorService();
    final TipoExameService tipoExameService = new TipoExameService();
    final CertificadoService certificadoService = new CertificadoService();
    final PendenciaService pendenciaService = new PendenciaService();
    int diasVencimento = 0;


    @FXML
    private void initialize() {
        tabelaVencimentos.setVisible(false);
        mainService.loadInicial();
        setTodos();
        pendenciaService.varreduraPendencias();
    }

    private void setTodos() {
        exameService.carregarExamesVigentes();
        certificadoService.carregarCertificadosVigentes();
        tipoCertificadoService.carregarTiposCertificado();
        setLabels();
        setTabelaPrincipal();
        setTabelaSecundaria();
    }

    private void setLabels() {
        labelTodos.setText(String.valueOf(exameService.listarExamePorVencimento(183).size() +
                certificadoService.listarCertificadosPorVencimento(183).size()));

        labelVencidos.setText(String.valueOf(exameService.listarExamePorVencimento(0).size() +
                certificadoService.listarCertificadosPorVencimento(0).size()));

        labelVencemSemana.setText(String.valueOf(exameService.listarExamePorVencimento(7).size()
                + certificadoService.listarCertificadosPorVencimento(7).size()));

        labelVencemMes.setText(String.valueOf(exameService.listarExamePorVencimento(30).size() +
                certificadoService.listarCertificadosPorVencimento(30).size()));

        labelVencemSemestre.setText(String.valueOf(exameService.listarExamePorVencimento(182).size() +
                certificadoService.listarCertificadosPorVencimento(182).size()));
    }

    private void setTabelaPrincipal() {

        try {
            if (!funcionarioService.listarFuncionariosPorStatus(true).isEmpty()) {
                colunaFuncionarioGeral.setCellValueFactory(new PropertyValueFactory<>("nome"));
                colunaIdadeGeral.setCellValueFactory(funcionarioStringCellDataFeatures -> {
                    Funcionario f = funcionarioStringCellDataFeatures.getValue();
                    int idade = funcionarioService.calcularIdade(f.getDataNascimento());
                    return new ReadOnlyObjectWrapper<>(idade).asString();
                });
                colunaSetorGeral.setCellValueFactory(f -> {
                    return new SimpleStringProperty(f.getValue().getSetor().getArea());
                });
                colunaAniversario.setCellValueFactory(funcionarioStringCellDataFeatures -> {
                    Funcionario f = funcionarioStringCellDataFeatures.getValue();
                    String dataAniversario = f.getDataNascimento().format(DateTimeFormatter.ofPattern("dd/MM"));
                    return new SimpleStringProperty(dataAniversario);
                });
                colunaStatusGeral.setCellValueFactory(cd -> new ReadOnlyStringWrapper("")); // valor dummy

                colunaStatusGeral.setCellFactory(col -> new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() < 0) {
                            setText(null);
                        } else {
                            Funcionario f = getTableView().getItems().get(getIndex());
                            if (pendenciaService.listaPendenciasPorFuncionario(f).isEmpty()) {
                                setText("OK");
                            } else {
                                setText("Pendências: " + pendenciaService.listaPendenciasPorFuncionario(f));
                            }
                        }
                    }
                });
                colunaAcoesGeral.setCellFactory(coluna -> new TableCell<>() {
                    final FontIcon iconeLancar = new FontIcon(FontAwesomeSolid.CHECK);
                    final Button btnLancarPendencia = new Button();

                    final FontIcon iconeEditar = new FontIcon(FontAwesomeSolid.EDIT);
                    final Button btnEditarFuncionario = new Button();

                    private final HBox hBox = new HBox(15, btnLancarPendencia, btnEditarFuncionario);

                    @Override
                    protected void updateItem(Node node, boolean b) {
                        hBox.setAlignment(Pos.CENTER);
                        super.updateItem(node, b);
                        if (b) {
                            setGraphic(null);
                        } else {
                            try {
                                mainService.loadInicial();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            btnEditarFuncionario.setGraphic(iconeEditar);
                            btnLancarPendencia.setGraphic(iconeLancar);
                            btnLancarPendencia.setDisable(true);
                            btnEditarFuncionario.setOnAction(e -> {
                                try {
                                    editarFuncionario(getTableRow().getItem());
                                } catch (Exception ex) {
                                    throw new RuntimeException(ex);
                                }
                            });
                            Funcionario f = getTableView().getItems().get(getIndex());
                            if (!pendenciaService.listaPendenciasPorFuncionario(f).isEmpty()) {
                                if (pendenciaService.getPendenciaFuncionario(f).tipoDe().getClass().equals(TipoExame.class)) {
                                    TipoExame tipoExame = (TipoExame) pendenciaService.getPendenciaFuncionario(f).tipoDe();
                                    btnLancarPendencia.setOnAction(e -> {
                                        Exame exame = mainService.getExameVencido(getTableRow().getItem());
                                        if (exame != null) {
                                            handleLancarPendecia(exame, btnLancarPendencia);
                                        } else {
                                            exame = Exame.ExameBuilder.builder()
                                                    .id(null)
                                                    .tipoExame(tipoExame)
                                                    .funcionario(getTableRow().getItem())
                                                    .dataEmissao(null)
                                                    .dataValidade(null)
                                                    .atualizadoPor(null)
                                                    .build();
                                            handleLancarPendecia(exame, btnLancarPendencia);
                                        }
                                    });
                                } else {
                                    TipoCertificado tipoCertificado = (TipoCertificado) pendenciaService.getPendenciaFuncionario(f).tipoDe();
                                    btnLancarPendencia.setOnAction(e -> {
                                        Certificado certificado = mainService.getCetificadoVencido(getTableRow().getItem());
                                        if (certificado != null) {
                                            handleLancarPendecia(certificado, btnLancarPendencia);
                                        } else {
                                            certificado = Certificado.CertificadoBuilder.builder()
                                                    .id(null)
                                                    .tipoCertificado(tipoCertificado)
                                                    .funcionario(getTableRow().getItem())
                                                    .dataEmissao(null)
                                                    .dataValidade(null)
                                                    .atualizadoPor(null)
                                                    .build();
                                            handleLancarPendecia(certificado, btnLancarPendencia);
                                        }
                                    });
                                }
                                btnLancarPendencia.setDisable(false);
                            }
                            setGraphic(hBox);
                        }
                    }
                });
                tabelaPrincipal.setItems(funcionarioService.listarFuncionariosPorStatus(true));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setTabelaSecundaria() {
        try {
            colunaFuncionarioVencimentos.setCellValueFactory(f -> {
                return new SimpleStringProperty(f.getValue().getFuncionario().getNome());
            });
            colunaIdadeVencimentos.setCellValueFactory(f -> {
                String idadeFuncionario =
                        String.valueOf(funcionarioService.calcularIdade(f.getValue().getFuncionario().getDataNascimento()));
                return new SimpleStringProperty(idadeFuncionario);
            });
            colunaSetorVencimentos.setCellValueFactory(f -> {
                return new SimpleStringProperty(f.getValue().getFuncionario().getSetor().getArea());
            });
            colunaTipoVencimentos.setCellValueFactory(f -> {
                String tipo = f.getValue().getClass().getSimpleName();
                return new SimpleStringProperty(tipo);
            });
            colunaDescricaoVencimentos.setCellValueFactory(f -> {
                String tipo = f.getValue().getClass().getSimpleName();
                String descricao = "";
                if (tipo.equals("Exame")) {
                    descricao = mainService.descreveTipo((Exame) f.getValue());
                }
                if (tipo.equals("Certificado")) {
                    descricao = mainService.descreveTipo((Certificado) f.getValue());
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
                LocalDate validade = f.getValue().getDataValidade() != null ? f.getValue().getDataValidade() : null;
                return new SimpleObjectProperty<>(validade);
            });
            colunaValidadeVencimentos.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : DateTimeFormatter.ofPattern("dd/MM/yyyy").format(item));
                }
            });
            colunaStatusVencimentos.setCellValueFactory(f -> new SimpleStringProperty(exameService.vencimentos(f.getValue())));
            colunaAcoesVencimentos.setCellFactory(coluna -> new TableCell<>() {
                final FontIcon iconeOpcoes = new FontIcon(FontAwesomeSolid.LIST);
                final FontIcon iconeLancar = new FontIcon(FontAwesomeSolid.CHECK);
                final Button btnLancarNovoTipo = new Button();
                final Button btnOpcoes = new Button();

                private final HBox hBox = new HBox(15, btnLancarNovoTipo, btnOpcoes);

                @Override
                protected void updateItem(Node node, boolean b) {
                    hBox.setAlignment(Pos.CENTER);
                    super.updateItem(node, b);
                    if (b || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                        setGraphic(null);
                        return;
                    }
                    Tipo linha = coluna.getTableView().getItems().get(getIndex());
                    ObservableValue<?> valorColuna = colunaTipoVencimentos.getCellObservableValue(linha);
                    String tipo = String.valueOf(valorColuna.getValue());

                    if (tipo.equals("Exame")) {
                        btnOpcoes.setGraphic(iconeOpcoes);
                        btnOpcoes.setOnAction(e -> handleOpcoes(getTableView().getItems().get(getIndex()), btnOpcoes, tipo));
                        btnLancarNovoTipo.setGraphic(iconeLancar);
                        btnLancarNovoTipo.setOnAction(e -> {
                            Exame exame = (Exame) getTableView().getItems().get(getIndex());
                            handleLancarPendecia(exame, btnLancarNovoTipo);
                        });
                        setGraphic(hBox);
                    }
                    if (tipo.equals("Certificado")) {
                        btnOpcoes.setGraphic(iconeOpcoes);
                        btnOpcoes.setOnAction(e -> handleOpcoes(getTableView().getItems().get(getIndex()), btnOpcoes, tipo));
                        btnLancarNovoTipo.setGraphic(iconeLancar);
                        btnLancarNovoTipo.setOnAction(e -> {
                            Certificado certificado = (Certificado) getTableView().getItems().get(getIndex());
                            handleLancarPendecia(certificado, btnLancarNovoTipo);
                        });
                        setGraphic(hBox);
                    }
                }
            });


            ObservableList<Tipo> listaTudo = FXCollections.observableArrayList();
            listaTudo.addAll(exameService.listarExamePorVencimento(diasVencimento));
            listaTudo.addAll(certificadoService.listarCertificadosPorVencimento(diasVencimento));
            tabelaVencimentos.setItems(listaTudo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void editarFuncionario(Funcionario funcionario) {
        if (funcionario != null) {
            new Janela<>().abrirJanela("/view/FuncionarioView.fxml", "Editar funcionario", MainApp.STAGE_PRINCIPAL, this::setTodos, funcionario);
        }
    }

    @FXML
    private void handleLancarPendecia(Exame exame, Node anchor) {
        Label label = new Label("Regularizar: " + exame.getTipoExame().getNome());
        Label label1 = new Label("Data de emissão");
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());
        Label label2 =
                new Label("Data de validade: " + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(exameService.calcularValidadeExame(exame.getFuncionario(),
                        datePicker.getValue(),
                        exame.getTipoExame())));
        Button btnConfirmar = new Button("Concluir");
        Label label3 = new Label("Condições atendidas: " + exame.getTipoExame().getNome());

        btnConfirmar.setOnAction(e -> {
            btnConfirmar.setDisable(true);
            Exame exame1 = Exame.ExameBuilder.builder()
                    .id(null)
                    .tipoExame(exame.getTipoExame())
                    .funcionario(exame.getFuncionario())
                    .dataEmissao(datePicker.getValue())
                    .dataValidade(exameService.calcularValidadeExame(exame.getFuncionario(), datePicker.getValue(),
                            exame.getTipoExame()))
                    .atualizadoPor(null)
                    .build();
            exame1 = exameService.lancarExame(exame1);
            if (exame.getId() != null) {
                Exame exame2 = Exame.ExameBuilder.builder()
                        .id(exame.getId())
                        .tipoExame(exame.getTipoExame())
                        .funcionario(exame.getFuncionario())
                        .dataEmissao(exame.getDataEmissao())
                        .dataValidade(exame.getDataValidade())
                        .atualizadoPor(exame1.getId())
                        .build();
                exameService.editarExame(exame2);
            }
            setTodos();
        });
        btnConfirmar.setDisable(false);
        VBox layout = new VBox(10, label, label1, datePicker, label2, btnConfirmar);
        layout.setPadding(new Insets(10));
        PopOver popOver = new PopOver(layout);
        popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);
        popOver.setDetachable(false);
        popOver.show(anchor);

    }

    @FXML
    private void handleLancarPendecia(Certificado certificado, Node anchor) {
        Label label = new Label("Regularizar: " + certificado.getTipoCertificado().getNome());
        Label label1 = new Label("Data de emissão");
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());
        Button btnConfirmar = new Button("Concluir");
        btnConfirmar.setOnAction(e -> {
            btnConfirmar.setDisable(true);
            Certificado certificado1 = Certificado.CertificadoBuilder.builder()
                    .id(null)
                    .tipoCertificado(certificado.getTipoCertificado())
                    .funcionario(certificado.getFuncionario())
                    .dataEmissao(datePicker.getValue())
                    .dataValidade(certificadoService.calcularValidade(datePicker.getValue(),
                            certificado.getTipoCertificado()))
                    .atualizadoPor(null)
                    .build();
            certificado1 = certificadoService.cadastrarCertificado(certificado1);
            if (certificado.getId() != null) {
                Certificado certificado2 = Certificado.CertificadoBuilder.builder()
                        .id(certificado.getId())
                        .tipoCertificado(certificado.getTipoCertificado())
                        .funcionario(certificado.getFuncionario())
                        .dataEmissao(certificado.getDataEmissao())
                        .dataValidade(certificado.getDataValidade())
                        .atualizadoPor(certificado1.getId())
                        .build();
                certificadoService.cadastrarCertificado(certificado2);
            }
            setTodos();
        });
        btnConfirmar.setDisable(false);
        VBox layout = new VBox(10, label, label1, datePicker, btnConfirmar);
        layout.setPadding(new Insets(10));
        PopOver popOver = new PopOver(layout);
        popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);
        popOver.setDetachable(false);
        popOver.show(anchor);
    }

    @FXML
    private void handleOpcoes(Tipo tipo, Node anchor, String tipoDescritivo) {
        FontIcon iconeLixeira = new FontIcon(FontAwesomeSolid.TRASH);
        FontIcon iconeEditar = new FontIcon(FontAwesomeSolid.PENCIL_ALT);
        Button btnEditarExame = new Button();
        Button btnDeletarExame = new Button();
        btnEditarExame.setGraphic(iconeEditar);
        btnDeletarExame.setGraphic(iconeLixeira);
        if (tipoDescritivo.equals("Exame")) {
            btnDeletarExame.setOnAction(e -> handleDeletarExame((Exame) tipo));
            btnEditarExame.setOnAction(event -> {
                try {
                    handleEditarExame((Exame) tipo);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        if (tipoDescritivo.equals("Certificado")) {
            btnDeletarExame.setOnAction(e -> handleDeletarCertificado((Certificado) tipo));
            btnEditarExame.setOnAction(event -> {
                try {
                    handleEditarCertificado((Certificado) tipo);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        HBox layout = new HBox(10, btnEditarExame, btnDeletarExame);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));
        PopOver popOver = new PopOver(layout);
        popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);
        popOver.setDetachable(false);
        popOver.show(anchor);
    }


    @FXML
    public void handleAbrirSetor() {
        new Janela<>().abrirJanela("/view/SetorView.fxml", "Cadastro de Setores", MainApp.STAGE_PRINCIPAL, this::setTodos, null);
    }

    @FXML
    public void handleAbrirGerenciarSetor() {
        ObservableList<Setor> setores = FXCollections.observableArrayList(setorService.listarSetores());

        String titulo = "Setores";
        JanelaGerenciar<Setor> controller =
                new Janela<Setor>().abrirJanelaGerenciar(
                        titulo,
                        MainApp.STAGE_PRINCIPAL,
                        this::setTodos
                );

        controller.configurar(
                titulo,
                "/view/SetorView.fxml",
                setores,
                List.of(
                        new Coluna<>("area", Setor::getArea)
                ),
                (setorService::listarSetores),
                (setorService::deletarSetor),
                false
        );
    }

    @FXML
    private void handleAbrirFuncionario(ActionEvent event) {
        new Janela<>().abrirJanela("/view/FuncionarioView.fxml", "Cadastro de Funcionários", MainApp.STAGE_PRINCIPAL, this::setTodos, null);
    }

    @FXML
    public void handleAbrirGerenciarFuncionario() {
        ObservableList<Funcionario> funcionarios = FXCollections.observableArrayList(funcionarioService.listarFuncionariosPorStatus(true));

        String titulo = "Funcionários";
        JanelaGerenciar<Funcionario> controller =
                new Janela<Funcionario>().abrirJanelaGerenciar(
                        titulo,
                        MainApp.STAGE_PRINCIPAL,
                        this::setTodos
                );

        controller.configurar(
                titulo,
                "/view/FuncionarioView.fxml",
                funcionarios,
                List.of(
                        new Coluna<>("Nome", Funcionario::getNome),
                        new Coluna<>("Cpf", Funcionario::getCpf),
                        new Coluna<>("Data Nascimento", f -> FormataData.br(f.getDataNascimento())),
                        new Coluna<>("Setor", Funcionario::getSetor),
                        new Coluna<>("Data Admissão", f -> FormataData.br(f.getDataAdmissao())),
                        new Coluna<>("Ativo", f -> f.getAtivo() ? "✔" : "✖"
                        )

                ),
                (funcionarioService::listarFuncionariosPorStatus),
                (funcionarioService::ativarInativar),
                true
        );
    }

    @FXML
    public void handleAbrirParticularidades() {
        new Janela<>().abrirJanela("/view/ParticularidadeView.fxml", "Cadastro de Particularidades", MainApp.STAGE_PRINCIPAL, this::setTodos, null);
    }

    @FXML
    public void handleAbrirGerenciarParticularidades() {
        final ParticularidadeService particularidadeService = new ParticularidadeService();
        ObservableList<Particularidade> particularidades = FXCollections.observableArrayList(particularidadeService.listarTodasParticularidades());
        String titulo = "Particularidades";

        JanelaGerenciar<Particularidade> controller =
                new Janela<Particularidade>().abrirJanelaGerenciar(
                        titulo,
                        MainApp.STAGE_PRINCIPAL,
                        this::setTodos
                );
        controller.configurar(
                titulo,
                "/view/ParticularidadeView.fxml",
                particularidades,
                List.of(
                        new Coluna<>("Particularidade", Particularidade::getNome),
                        new Coluna<>("Tipo Exame", f -> f.getTipoExame().getNome()),
                        new Coluna<>("Periodicidade", Particularidade::getPeriodicidade)
                ),
                (particularidadeService::listarTodasParticularidades),
                (particularidadeService::deletarParticularidade),
                false
        );
    }

    @FXML
    public void handleAbrirVincularParticularidades() {
        new Janela<>().abrirJanela("/view/VinculoParticularidadesFuncionarios.fxml", "Vincular Particularidades", MainApp.STAGE_PRINCIPAL,
                this::setTodos, null);
    }

    @FXML
    public void handleAbrirGerenciarVinculos() {
        final ParticularidadeService particularidadeService = new ParticularidadeService();
        ObservableList<VinculoFuncionarioParticularidade> vinculoFuncionarioParticularidade =
                FXCollections.observableArrayList(particularidadeService.listarTodosVinculos(true));
        String titulo = "Vinculos Funcionarios Particularidades";

        JanelaGerenciar<VinculoFuncionarioParticularidade> controller =
                new Janela<VinculoFuncionarioParticularidade>().abrirJanelaGerenciar(
                        titulo,
                        MainApp.STAGE_PRINCIPAL,
                        this::setTodos
                );

        controller.configurar(
                titulo,
                "/view/VinculoParticularidadesFuncionarios.fxml",
                vinculoFuncionarioParticularidade,
                List.of(
                        new Coluna<>("Funcionário", f -> f.getFuncionario().getNome()),
                        new Coluna<>("Particularidade", f -> f.getParticularidade().getNome()),
                        new Coluna<>("Tipo Exame", f -> f.getParticularidade().getTipoExame().getNome()),
                        new Coluna<>("Motivo", VinculoFuncionarioParticularidade::getMotivo),
                        new Coluna<>("Data Vinculo", f -> FormataData.br(f.getDataInclusao())),
                        new Coluna<>("Data Desvinculo", f -> FormataData.br(f.getDataExclusao()))
                ),
                (particularidadeService::listarTodosVinculos),
                (particularidadeService::ativarInativarVinculo),
                true
        );
    }

    @FXML
    public void handleAbrirExame() {
        new Janela<>().abrirJanela("/view/TipoExameView.fxml", "Cadastro Tipo Exame", MainApp.STAGE_PRINCIPAL, this::setTodos, null);
    }

    @FXML
    public void handleAbrirGerenciarExame() {

        ObservableList<TipoExame> tipoExames =
                FXCollections.observableArrayList(
                        tipoExameService.listarTiposExame()
                );

        String titulo = "Tipos Exame";

        JanelaGerenciar<TipoExame> controller =
                new Janela<TipoExame>().abrirJanelaGerenciar(
                        titulo,
                        MainApp.STAGE_PRINCIPAL,
                        this::setTodos
                );

        controller.configurar(
                titulo,
                "/view/TipoExameView.fxml",
                tipoExames,
                List.of(
                        new Coluna<>("Tipo Exame", TipoExame::getNome)
                ),
                tipoExameService::listarTiposExame,
                tipoExameService::deletarTipoExame,
                false
        );
    }

    @FXML
    public void handleDeletarExame(Exame exame) {
        exameService.deletarExame(exame.getId());
        setTodos();
    }

    @FXML
    public void handleEditarExame(Exame exame) {
        if (exame != null) {
            new Janela<>().abrirJanela("/view/ExamesView.fxml", "Editar exame", MainApp.STAGE_PRINCIPAL, this::setTodos, exame);
        }
    }

    @FXML
    public void handleEditarCertificado(Certificado certificado) {
        if (certificado != null) {
            new Janela<>().abrirJanela("/view/CertificadosView.fxml", "Editar exame", MainApp.STAGE_PRINCIPAL, this::setTodos, certificado);
        }
    }

    @FXML
    public void handleDeletarCertificado(Certificado certificado) {
        certificadoService.deletarCertificado(certificado);
        setTodos();
    }

    @FXML
    public void handleTipoCertificado() {
        new Janela<>().abrirJanela("/view/TipoCertificadoView.fxml", "Cadastro Tipo Certificado", MainApp.STAGE_PRINCIPAL, this::setTodos, null);
    }

    @FXML
    public void handleGerenciarTiposCertificado() {
        ObservableList<TipoCertificado> tipoCertificados =
                FXCollections.observableArrayList(tipoCertificadoService.listarTiposCertificados());

        String titulo = "Tipos Certificado";
        JanelaGerenciar<TipoCertificado> controller =
                new Janela<TipoCertificado>().abrirJanelaGerenciar(
                        titulo,
                        MainApp.STAGE_PRINCIPAL,
                        this::setTodos
                );

        controller.configurar(
                titulo,
                "/view/TipoCertificadoView.fxml",
                tipoCertificados,
                List.of(
                        new Coluna<>("Tipo Certificado", TipoDe::getNome)
                ),
                (tipoCertificadoService::listarTiposCertificados),
                (tipoCertificadoService::deletarTipoCertificado),
                false
        );
    }

    @FXML
    public void handleLancarCertificado() {
        try {
            tipoCertificadoService.carregarTiposCertificado();
            if (tipoCertificadoService.listarTiposCertificados().isEmpty()) {
                Notifications.create()
                        .owner(janela.stage)
                        .title("Atenção")
                        .text("Nenhum Tipo Certificado Cadastrado")
                        .hideAfter(Duration.seconds(3))
                        .showError();
                return;
            }
            new Janela<>().abrirJanela("/view/CertificadosView.fxml", "Lançar Certificados", MainApp.STAGE_PRINCIPAL, this::setTodos, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleLancarExame() {
        try {
            if (tipoExameService.listarTiposExame().isEmpty()) {
                Notifications.create()
                        .owner(janela.stage)
                        .title("Atenção")
                        .text("Nenhum Tipo Exame Cadastrado")
                        .hideAfter(Duration.seconds(3))
                        .showError();
                return;
            }
            new Janela<>().abrirJanela("/view/ExamesView.fxml", "Lançar Exames", MainApp.STAGE_PRINCIPAL, this::setTodos, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleAbrirRelatorioFuncionario() {
        new Janela<>().abrirJanela("/view/RelatoriosPorFuncionariosView.fxml", "Relatórios por Funcionários", MainApp.STAGE_PRINCIPAL,
                this::setTodos, null);
    }

    public void handleAbrirAjuda(ActionEvent event) {
        new Janela<>().abrirJanela("/view/DuvidasErrosView.fxml", "Ajuda - Dúvidas - Erros", MainApp.STAGE_PRINCIPAL,
                this::setTodos, null);
    }

    public void handleBtnVencidos() {
        tabelaPrincipal.setVisible(false);
        tabelaVencimentos.setVisible(true);
        diasVencimento = 0;
        setLabels();
        setTabelaSecundaria();
        tabelaVencimentos.refresh();
    }

    public void handleBtnSemana() {
        tabelaPrincipal.setVisible(false);
        tabelaVencimentos.setVisible(true);
        diasVencimento = 7;
        setLabels();
        setTabelaSecundaria();
        tabelaVencimentos.refresh();
    }

    public void handleBtnMes() {
        tabelaPrincipal.setVisible(false);
        tabelaVencimentos.setVisible(true);
        diasVencimento = 30;
        setTabelaSecundaria();
        setLabels();
    }

    public void handleBtnSemestre() {
        tabelaPrincipal.setVisible(false);
        tabelaVencimentos.setVisible(true);
        diasVencimento = 182;
        setLabels();
        setTabelaSecundaria();
        tabelaVencimentos.refresh();
    }

    public void handleBtnTodos() {
        tabelaVencimentos.setVisible(true);
        tabelaPrincipal.setVisible(false);
        diasVencimento = 183;
        setLabels();
        setTabelaSecundaria();
        tabelaVencimentos.refresh();
    }

    public void handleBtnGeral() {
        mainService.loadInicial();
        initialize();
        setTabelaPrincipal();
        setLabels();
        tabelaPrincipal.setVisible(true);
        tabelaVencimentos.setVisible(false);
        tabelaPrincipal.refresh();
        pendenciaService.varreduraPendencias();
    }

}
