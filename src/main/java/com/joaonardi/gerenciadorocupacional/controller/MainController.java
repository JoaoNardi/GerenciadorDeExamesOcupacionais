package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.*;
import com.joaonardi.gerenciadorocupacional.service.*;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import org.controlsfx.control.PopOver;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
    public TableColumn<Tipo, String> colunaEmissaoVencimentos;
    public TableColumn<Tipo, String> colunaValidadeVencimentos;
    public TableColumn<Tipo, String> colunaStatusVencimentos;
    public TableColumn<Tipo, Node> colunaAcoesVencimentos;


    Janela janela = new Janela();

    TipoCertificadoService tipoCertificadoService = new TipoCertificadoService();
    FuncionarioService funcionarioService = new FuncionarioService();
    ExameService exameService = new ExameService();
    MainService mainService = new MainService();
    SetorService setorService = new SetorService();
    TipoExameService tipoExameService = new TipoExameService();
    CertificadoService certificadoService = new CertificadoService();
    int diasVencimento = 0;

    @FXML
    private void initialize() {
        exameService.carregarExamesVigentes();
        certificadoService.carregarCertificadosVigentes();
        tipoExameService.carregarTipoExames();
        tipoCertificadoService.carregarTiposCertificado();
        tabelaVencimentos.setVisible(false);
        MainService.loadInicial();
        setTodos();
    }

    private void setTodos() {
        setLabels();
        setTabelaPrincipal();
    }

    private void setLabels() {
        labelTodos.setText(String.valueOf(exameService.listarExamePorVencimento(183).size() +
                certificadoService.listarCertificadosPorVencimento(183).size()));

        labelVencidos.setText(String.valueOf(exameService.listarExamePorVencimento(0).size() +
                certificadoService.listarCertificadosPorVencimento(0).size()));

        labelVencemSemana.setText(String.valueOf(exameService.listarExamePorVencimento(7).size()
                +certificadoService.listarCertificadosPorVencimento(7).size()));

        labelVencemMes.setText(String.valueOf(exameService.listarExamePorVencimento(30).size() +
                certificadoService.listarCertificadosPorVencimento(30).size()));

        labelVencemSemestre.setText(String.valueOf(exameService.listarExamePorVencimento(182).size() +
                certificadoService.listarCertificadosPorVencimento(182).size()));
    }

    private void setTabelaPrincipal() {
        funcionarioService.carregarFuncionariosPorStatus(true);
        try {
            if (!funcionarioService.listarFuncionarios().isEmpty()) {
                colunaFuncionarioGeral.setCellValueFactory(new PropertyValueFactory<>("nome"));
                colunaIdadeGeral.setCellValueFactory(funcionarioStringCellDataFeatures -> {
                    Funcionario f = funcionarioStringCellDataFeatures.getValue();
                    int idade = funcionarioService.calcularIdade(f.getDataNascimento());
                    return new ReadOnlyObjectWrapper<>(idade).asString();
                });
                colunaSetorGeral.setCellValueFactory(f -> {
                    String areaSetor = setorService.getSetorMapeado(f.getValue().getIdSetor());
                    return new SimpleStringProperty(areaSetor);
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
                            if (mainService.verificaStatusFuncionario(f) != null) {
                                setText(
                                        "Pendencias: " +
                                                mainService.verificaStatusFuncionario(f).stream()
                                                        .map(TipoDe::getNome).toList().toString()
                                                        .replace("[", "").replace("]", ""));
                            } else {
                                setText("Ok");
                            }
                        }
                    }
                });
                colunaAcoesGeral.setCellFactory(coluna -> new TableCell<>() {
                    final FontIcon iconeLancar = new FontIcon(FontAwesomeSolid.CHECK);
                    final Button btnLancarPendencia = new Button();

                    final FontIcon iconeEditar = new FontIcon(FontAwesomeSolid.EDIT);
                    final Button btnEditarFuncionario = new Button();

                    private final HBox hBox = new HBox(10, btnLancarPendencia, btnEditarFuncionario);

                    @Override
                    protected void updateItem(Node node, boolean b) {
                        super.updateItem(node, b);
                        if (b) {
                            setGraphic(null);
                        } else {
                            try {
                                MainService.loadInicial();
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
                            if (mainService.verificaStatusFuncionario(getTableRow().getItem()) != null) {
                                if (mainService.verificaStatusFuncionario(getTableRow().getItem()).getFirst().getClass().equals(TipoExame.class)) {
                                    TipoExame tipoExame = (TipoExame) mainService.verificaStatusFuncionario(getTableRow().getItem()).getFirst();
                                    btnLancarPendencia.setOnAction(e -> {
                                        Exame exame = mainService.getExameVencido(getTableRow().getItem());
                                        if (exame != null) {
                                            handleLancarPendecia(exame, btnLancarPendencia);
                                        } else if (exame == null) {
                                            exame = Exame.ExameBuilder.builder()
                                                    .id(null)
                                                    .idTipoExame(tipoExame.getId())
                                                    .idFuncionario(getTableRow().getItem().getId())
                                                    .dataEmissao(null)
                                                    .dataValidade(null)
                                                    .atualizadoPor(null)
                                                    .build();
                                            handleLancarPendecia(exame, btnLancarPendencia);
                                        }
                                    });
                                } else {
                                    TipoCertificado tipoCertificado = (TipoCertificado) mainService.verificaStatusFuncionario(getTableRow().getItem()).getFirst();
                                    btnLancarPendencia.setOnAction(e -> {
                                        Certificado certificado = mainService.getCetificadoVencido(getTableRow().getItem());
                                        if (certificado != null) {
                                            handleLancarPendecia(certificado, btnLancarPendencia);
                                        } else if (certificado == null) {
                                            certificado = Certificado.CertificadoBuilder.builder()
                                                    .id(null)
                                                    .idTipoCertificado(tipoCertificado.getId())
                                                    .idFuncionario(getTableRow().getItem().getId())
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
                tabelaPrincipal.setItems(funcionarioService.listarFuncionarios());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setTabelaSecundaria() {
        try {
            colunaFuncionarioVencimentos.setCellValueFactory(f -> {
                String nomeFuncionario = String.valueOf(funcionarioService.getFuncionarioMapeadoPorId(f.getValue().getIdFuncionario()).getNome());
                return new SimpleStringProperty(nomeFuncionario);
            });
            colunaIdadeVencimentos.setCellValueFactory(f -> {
                String idadeFuncionario =
                        String.valueOf(funcionarioService.calcularIdade(funcionarioService.getFuncionarioMapeadoPorId(f.getValue().getIdFuncionario()).getDataNascimento()));
                return new SimpleStringProperty(idadeFuncionario);
            });
            colunaSetorVencimentos.setCellValueFactory(f -> {
                int setorId = funcionarioService.getFuncionarioMapeadoPorId(f.getValue().getIdFuncionario()).getIdSetor();
                String setorNome = String.valueOf(setorService.getSetorMapeado(setorId));
                return new SimpleStringProperty(setorNome);
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
                String emissao = f.getValue().getDataEmissao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                return new SimpleStringProperty(emissao);
            });
            colunaValidadeVencimentos.setCellValueFactory(f -> {
                String validade = f.getValue().getDataValidade() != null ? f.getValue().getDataValidade().format(DateTimeFormatter.ofPattern("dd/MM" +
                        "/yyyy")) : null;
                return new SimpleStringProperty(validade);
            });
            colunaStatusVencimentos.setCellValueFactory(f -> {
                return new SimpleStringProperty(exameService.vencimentos(f.getValue()));
            });
            colunaAcoesVencimentos.setCellFactory(coluna -> new TableCell<>() {
                final FontIcon iconeOpcoes = new FontIcon(FontAwesomeSolid.LIST);
                final FontIcon iconeLancar = new FontIcon(FontAwesomeSolid.CHECK);
                Button btnLancarNovoTipo = new Button();
                Button btnOpcoes = new Button();

                private final HBox hBox = new HBox(10, btnLancarNovoTipo, btnOpcoes);


                @Override
                protected void updateItem(Node node, boolean b) {
                    super.updateItem(node, b);
                    if (b || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                        setGraphic(null);
                        return;
                    }
                    Tipo linha = coluna.getTableView().getItems().get(getIndex());
                    ObservableValue<?> valorColuna = colunaTipoVencimentos.getCellObservableValue(linha);
                    String tipo = String.valueOf(valorColuna.getValue());

                    if (tipo.equals("Exame")) {
                        if (b) {
                            setGraphic(null);
                        } else {
                            btnOpcoes.setGraphic(iconeOpcoes);
                            btnOpcoes.setOnAction(e -> {
                                handleOpcoes(getTableView().getItems().get(getIndex()), btnOpcoes, tipo);

                            });
                            btnLancarNovoTipo.setGraphic(iconeLancar);
                            btnLancarNovoTipo.setOnAction(e -> {
                                Exame exame = (Exame) getTableView().getItems().get(getIndex());
                                handleLancarPendecia(exame, btnLancarNovoTipo);
                            });
                            setGraphic(hBox);
                        }
                    }
                    if (tipo.equals("Certificado")) {
                        if (b) {
                            setGraphic(null);
                        } else {
                            btnOpcoes.setGraphic(iconeOpcoes);
                            btnOpcoes.setOnAction(e -> {
                                handleOpcoes(getTableView().getItems().get(getIndex()), btnOpcoes, tipo);

                            });
                            btnLancarNovoTipo.setGraphic(iconeLancar);
                            btnLancarNovoTipo.setOnAction(e -> {
                                Certificado certificado = (Certificado) getTableView().getItems().get(getIndex());
                                handleLancarPendecia(certificado, btnLancarNovoTipo);
                            });
                            setGraphic(hBox);
                        }
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
    public void editarFuncionario(Funcionario funcionario) throws Exception {
        if (funcionario != null) {
            janela.abrirJanela("/view/FuncionarioView.fxml", "Editar funcionario", null);
            funcionarioController = janela.loader.getController();
            funcionarioController.setFuncionario(funcionario);
        }
    }

    @FXML
    private void handleLancarPendecia(Exame exame, Node anchor) {
        Label label = new Label("Regularizar: " + tipoExameService.getTipoExameMapeadoPorId(exame.getIdTipoExame()).getNome());
        Label label1 = new Label("Data de emissão");
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());
        Button btnConfirmar = new Button("Concluir");
        btnConfirmar.setOnAction(e -> {
            Exame exame1 = Exame.ExameBuilder.builder()
                    .id(null)
                    .idTipoExame(exame.getIdTipoExame())
                    .idFuncionario(exame.getIdFuncionario())
                    .dataEmissao(datePicker.getValue())
                    .dataValidade(exameService.calcularValidadeExame(funcionarioService.getFuncionarioMapeadoPorId(exame.getIdFuncionario()), datePicker.getValue(),
                            tipoExameService.getTipoExameMapeadoPorId(exame.getIdTipoExame())))
                    .atualizadoPor(null)
                    .build();
            exame1 = exameService.lancarExame(exame1);
            if (exame.getId() != null) {
                Exame exame2 = Exame.ExameBuilder.builder()
                        .id(exame.getId())
                        .idTipoExame(exame.getIdTipoExame())
                        .idFuncionario(exame.getIdFuncionario())
                        .dataEmissao(exame.getDataEmissao())
                        .dataValidade(exame.getDataValidade())
                        .atualizadoPor(exame1.getId())
                        .build();
                exameService.editarExame(exame2);
            }
        });

        VBox layout = new VBox(10, label, label1, datePicker, btnConfirmar);
        layout.setPadding(new Insets(10));
        PopOver popOver = new PopOver(layout);
        popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);
        popOver.setDetachable(false);
        popOver.show(anchor);
    }

    @FXML
    private void handleLancarPendecia(Certificado certificado, Node anchor) {
        Label label = new Label("Regularizar: " + tipoCertificadoService.getTipoCertificadoMapeadoPorId(certificado.getIdTipoCertificado()).getNome());
        Label label1 = new Label("Data de emissão");
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());
        Button btnConfirmar = new Button("Concluir");
        btnConfirmar.setOnAction(e -> {
            Certificado certificado1 = Certificado.CertificadoBuilder.builder()
                    .id(null)
                    .idTipoCertificado(certificado.getIdTipoCertificado())
                    .idFuncionario(certificado.getIdFuncionario())
                    .dataEmissao(datePicker.getValue())
                    .dataValidade(certificadoService.calcularValidade(datePicker.getValue(),
                            tipoCertificadoService.getTipoCertificadoMapeadoPorId(certificado.getIdTipoCertificado())))
                    .atualizadoPor(null)
                    .build();
            certificado1 = certificadoService.cadastrarCertificado(certificado1);
            if (certificado.getId() != null) {
                Certificado certificado2 = Certificado.CertificadoBuilder.builder()
                        .id(certificado.getId())
                        .idTipoCertificado(certificado.getIdTipoCertificado())
                        .idFuncionario(certificado.getIdFuncionario())
                        .dataEmissao(certificado.getDataEmissao())
                        .dataValidade(certificado.getDataValidade())
                        .atualizadoPor(certificado1.getId())
                        .build();
                certificadoService.editarCertificado(certificado2);
            }
        });

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
            btnDeletarExame.setOnAction(e -> {
                handleDeletarExame((Exame) tipo);
            });
            btnEditarExame.setOnAction(event -> {
                try {
                    handleEditarExame((Exame) tipo);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        if (tipoDescritivo.equals("Certificado")) {
            btnDeletarExame.setOnAction(e -> {
                handleDeletarCertificado((Certificado) tipo);
            });
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

    public void handleDeletarExame(Exame exame) {
        exameService.deletarExame(exame.getId());
        setTodos();
    }

    public void handleDeletarCertificado(Certificado certificado) {
        certificadoService.deletarCertificado(certificado);
        setTodos();
    }

    @FXML
    public void handleEditarExame(Exame exame) throws Exception {
        if (exame != null) {
            janela.abrirJanela("/view/ExamesView.fxml", "Editar exame", this::setTodos);
            examesController = janela.loader.getController();
            examesController.setExame(exame);
        }
    }

    @FXML
    public void handleEditarCertificado(Certificado certificado) throws Exception {
        if (certificado != null) {
            janela.abrirJanela("/view/CertificadosView.fxml", "Editar exame", this::setTodos);
            certificadoController = janela.loader.getController();
            certificadoController.setCertificado(certificado);
        }
    }


    @FXML
    private void handleAbrirFuncionario(ActionEvent event) {
        janela.abrirJanela("/view/FuncionarioView.fxml", "Cadastro de Funcionários", this::setTodos);
    }

    @FXML
    public void handleAbrirGerenciarFuncionario(ActionEvent event) {
        janela.abrirJanela("/view/GerenciarFuncionariosView.fxml", "Gerenciar Funcionarios", this::setTodos);
    }

    @FXML
    public void handleAbrirSetor(ActionEvent event) {
        janela.abrirJanela("/view/SetorView.fxml", "Cadastro de Setores", this::setTodos);
    }

    @FXML
    public void handleAbrirGerenciarSetor(ActionEvent event) {
        janela.abrirJanela("/view/GerenciarSetoresView.fxml", "Gerenciar Setores", this::setTodos);
    }

    @FXML
    public void handleAbrirExame() {
        janela.abrirJanela("/view/TipoExameView.fxml", "Cadastro de Exame", this::setTodos);
    }

    @FXML
    public void handleAbrirGerenciarExame(ActionEvent event) {
        janela.abrirJanela("/view/GerenciarExamesView.fxml", "Gerenciar Exames", this::setTodos);
    }

    public void handleTipoCertificado(ActionEvent event) {
        janela.abrirJanela("/view/TipoCertificadoView.fxml", "Cadastro Tipo Certificado", this::setTodos);
    }

    public void handleGerenciarTiposCertificado(ActionEvent event) {
        janela.abrirJanela("/view/GerenciarCertificadosView.fxml", "Gerenciar Tipos Certificados", this::setTodos);
    }

    public void handleLancarCertificado(ActionEvent event) {
        janela.abrirJanela("/view/CertificadosView.fxml", "Lançar Certificados", this::setTodos);
    }

    public void handleLancarPendecia(ActionEvent event) {
        janela.abrirJanela("/view/ExamesView.fxml", "Lançar Exames", this::setTodos);
    }

    public void handleAbrirRelatorioFuncionario(ActionEvent event) {
        janela.abrirJanela("/view/RelatoriosPorFuncionariosView.fxml", "Relatórios por Funcionários", this::setTodos);

    }

    public void handleBtnVencidos(ActionEvent event) {
        tabelaPrincipal.setVisible(false);
        tabelaVencimentos.setVisible(true);
        diasVencimento = 0;
        setLabels();
        setTabelaSecundaria();
        tabelaVencimentos.refresh();
    }

    public void handleBtnSemana(ActionEvent event) {
        tabelaPrincipal.setVisible(false);
        tabelaVencimentos.setVisible(true);
        diasVencimento = 7;
        setLabels();
        setTabelaSecundaria();
        tabelaVencimentos.refresh();
    }

    public void handleBtnMes(ActionEvent event) {
        tabelaPrincipal.setVisible(false);
        tabelaVencimentos.setVisible(true);
        diasVencimento = 30;
        setTabelaSecundaria();
        setLabels();
    }

    public void handleBtnSemestre(ActionEvent event) {
        tabelaPrincipal.setVisible(false);
        tabelaVencimentos.setVisible(true);
        diasVencimento = 182;
        setLabels();
        setTabelaSecundaria();
        tabelaVencimentos.refresh();
    }

    public void handleBtnTodos(ActionEvent event) {
        tabelaVencimentos.setVisible(true);
        tabelaPrincipal.setVisible(false);
        diasVencimento = 183;
        setLabels();
        setTabelaSecundaria();
        tabelaVencimentos.refresh();
    }

    public void handleBtnGeral(ActionEvent event) throws Exception {
        MainService.loadInicial();
        initialize();
        setTabelaPrincipal();
        setLabels();
        tabelaPrincipal.setVisible(true);
        tabelaVencimentos.setVisible(false);
        tabelaPrincipal.refresh();
    }
}
