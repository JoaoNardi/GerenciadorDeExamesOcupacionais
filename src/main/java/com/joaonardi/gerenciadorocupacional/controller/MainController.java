package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.cache.FuncionarioCache;
import com.joaonardi.gerenciadorocupacional.model.Exame;
import com.joaonardi.gerenciadorocupacional.model.Funcionario;
import com.joaonardi.gerenciadorocupacional.service.*;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
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
    public TableView<Exame> tabelaVencimentos;
    public TableColumn<Exame, String> colunaFuncionarioVencimentos;
    public TableColumn<Exame, String> colunaIdadeVencimentos;
    public TableColumn<Exame, String> colunaSetorVencimentos;
    public TableColumn<Exame, String> colunaTipoVencimentos;
    public TableColumn<Exame, String> colunaDescricaoVencimentos;
    public TableColumn<Exame, String> colunaEmissaoVencimentos;
    public TableColumn<Exame, String> colunaValidadeVencimentos;
    public TableColumn<Exame, String> colunaStatusVencimentos;
    public TableColumn<Exame, Node> colunaAcoesVencimentos;


    Janela janela = new Janela();

    FuncionarioService funcionarioService = new FuncionarioService();
    ExameService exameService = new ExameService();
    MainService mainService = new MainService();
    SetorService setorService = new SetorService();
    TipoExameService tipoExameService = new TipoExameService();
    int diasVencimento = 0;

    @FXML
    private void initialize() throws Exception {
        tabelaVencimentos.setVisible(false);
        MainService.loadInicial();
        setTodos();
    }

    private void setTodos() {
        setLabels();
        setTabelaPrincipal();
        setTabelaSecundaria();
    }

    private void setLabels() {
        labelTodos.setText(String.valueOf(exameService.listarExamePorVencimento(183).size()));
        labelVencidos.setText(String.valueOf(exameService.listarExamePorVencimento(0).size()));
        labelVencemSemana.setText(String.valueOf(exameService.listarExamePorVencimento(7).size()));
        labelVencemMes.setText(String.valueOf(exameService.listarExamePorVencimento(30).size()));
        labelVencemSemestre.setText(String.valueOf(exameService.listarExamePorVencimento(182).size()));
    }

    private void setTabelaPrincipal() {
        try {
            if (!funcionarioService.listarFuncionarios(true).isEmpty()) {
                colunaFuncionarioGeral.setCellValueFactory(new PropertyValueFactory<>("nome"));
                colunaIdadeGeral.setCellValueFactory(funcionarioStringCellDataFeatures -> {
                    Funcionario f = funcionarioStringCellDataFeatures.getValue();
                    int idade = funcionarioService.calcularIdade(f.getDataNascimento());
                    return new ReadOnlyObjectWrapper<>(idade).asString();
                });
                colunaSetorGeral.setCellValueFactory(f -> {
                    String areaSetor = setorService.getSetorMapeadoPorId(f.getValue().getIdSetor());
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
                            if (mainService.verificaStatusFuncionario2(f) != null) {
                                setText(
                                        "Exames Pendentes: " +
                                                mainService.verificaStatusFuncionario2(f).stream()
                                                        .map(tipoExame -> tipoExame.getNome()).toList().toString()
                                                        .replace("[", "").replace("]", ""));
                            } else {
                                setText("ok");
                            }
                        }
                    }
                });
                colunaAcoesGeral.setCellFactory(coluna -> new TableCell<>() {
                    final FontIcon iconeLancar = new FontIcon(FontAwesomeSolid.CHECK);
                    final Button btnLancarExameTipado = new Button();

                    final FontIcon iconeEditar = new FontIcon(FontAwesomeSolid.EDIT);
                    final Button btnEditarFuncionario = new Button();

                    private final HBox hBox = new HBox(10, btnLancarExameTipado, btnEditarFuncionario);

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
                            btnLancarExameTipado.setGraphic(iconeLancar);
                            btnLancarExameTipado.setDisable(true);
                            btnEditarFuncionario.setOnAction(e -> {
                                try {
                                    editarFuncionario(getTableRow().getItem());
                                } catch (Exception ex) {
                                    throw new RuntimeException(ex);
                                }
                            });
                            if (mainService.verificaStatusFuncionario2(getTableRow().getItem()) != null) {
                                int idTipoExame = mainService.verificaStatusFuncionario2(getTableRow().getItem()).getFirst().getId();
                                btnLancarExameTipado.setDisable(false);

                                btnLancarExameTipado.setOnAction(e -> {
                                    Exame exame = Exame.ExameBuilder.builder()
                                            .id(null)
                                            .idTipoExame(idTipoExame)
                                            .idFuncionario(getTableRow().getItem().getId())
                                            .dataEmissao(null)
                                            .dataValidade(null)
                                            .atualizadoPor(null)
                                            .build();
                                    handleLancarExame(exame, btnLancarExameTipado);
                                });
                            }
                            setGraphic(hBox);
                        }
                    }
                });
                tabelaPrincipal.setItems(funcionarioService.listarFuncionarios(true));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setTabelaSecundaria() {
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
            String setorNome = String.valueOf(setorService.getSetorMapeadoPorId(setorId));
            return new SimpleStringProperty(setorNome);
        });
        colunaTipoVencimentos.setCellValueFactory(f -> {
            String tipo = f.getValue().getClass().getSimpleName();
            return new SimpleStringProperty(tipo);
        });
        colunaDescricaoVencimentos.setCellValueFactory(f -> {
            String descricao = tipoExameService.getTipoExameMapeadoPorId(f.getValue().getIdTipoExame()).getNome();
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
            return new SimpleStringProperty(exameService.vencimentos(f));
        });
        colunaAcoesVencimentos.setCellFactory(coluna -> new TableCell<>() {
            final FontIcon iconeOpcoes = new FontIcon(FontAwesomeSolid.LIST);
            final FontIcon iconeLancar = new FontIcon(FontAwesomeSolid.CHECK);
            Button btnLancarNovoExame = new Button();
            Button btnOpcoesExame = new Button();

            private final HBox hBox = new HBox(10, btnLancarNovoExame, btnOpcoesExame);

            @Override
            protected void updateItem(Node node, boolean b) {
                super.updateItem(node, b);
                if (b) {
                    setGraphic(null);
                } else {
                    btnOpcoesExame.setGraphic(iconeOpcoes);
                    btnOpcoesExame.setOnAction(e -> {
                        handleOpcoesExame(getTableView().getItems().get(getIndex()), btnOpcoesExame);

                    });
                    btnLancarNovoExame.setGraphic(iconeLancar);
                    btnLancarNovoExame.setOnAction(e -> {
                        Exame exame = getTableView().getItems().get(getIndex());
                        handleLancarExame(exame, btnLancarNovoExame);
                    });
                    setGraphic(hBox);
                }
            }
        });


        tabelaVencimentos.setItems(exameService.listarExamePorVencimento(diasVencimento));
    }
    @FXML
    public void editarFuncionario(Funcionario funcionario) throws Exception {
        if (funcionario != null){
            janela.abrirJanela("/view/FuncionarioView.fxml", "Editar funcionario", null);
            funcionarioController = janela.loader.getController();
            funcionarioController.setFuncionario(funcionario);
        }
    }

    @FXML
    private void handleLancarExame(Exame exame, Node anchor) {
        Label label = new Label("Regularizar: " + tipoExameService.getTipoExameMapeadoPorId(exame.getIdTipoExame()));
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
                    .dataValidade(exameService.calcularValidadeExame(FuncionarioCache.getFuncionarioMapeado(exame.getIdFuncionario()), datePicker.getValue(),
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
    private void handleOpcoesExame(Exame exame, Node anchor) {
        FontIcon iconeLixeira = new FontIcon(FontAwesomeSolid.TRASH);
        FontIcon iconeEditar = new FontIcon(FontAwesomeSolid.PENCIL_ALT);
        Button btnEditarExame = new Button();
        Button btnDeletarExame = new Button();
        btnEditarExame.setGraphic(iconeEditar);
        btnDeletarExame.setGraphic(iconeLixeira);
        btnDeletarExame.setOnAction(e -> {
            handleDeletarExame(exame);
        });
        btnEditarExame.setOnAction(event -> {
            try {
                handleEditarExame(exame);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
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

    @FXML
    public void handleEditarExame(Exame exame) throws Exception {
        Exame exameSelecionado = exame;
        if (exameSelecionado != null) {
            janela.abrirJanela("/view/ExamesView.fxml", "Editar exame", this::setTodos);
            examesController = janela.loader.getController();
            examesController.setExame(exameSelecionado);
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

    public void handleGerenciarCertificados(ActionEvent event) {
        janela.abrirJanela("/view/CertificadoView.fxml", "Gerenciar Certificados", this::setTodos);
    }

    public void handleLancarExame(ActionEvent event) {
        janela.abrirJanela("/view/ExamesView.fxml", "Lançar Exames", this::setTodos);
    }

    public void handleGerenciarExames(ActionEvent event) {
    }

    public void handleBtnVencidos(ActionEvent event) {
        setTabelaSecundaria();
        setLabels();
        tabelaPrincipal.setVisible(false);
        tabelaVencimentos.setVisible(true);
        diasVencimento = 0;
        tabelaVencimentos.setItems(exameService.listarExamePorVencimento(diasVencimento));
    }

    public void handleBtnSemana(ActionEvent event) {
        setTabelaSecundaria();
        setLabels();
        tabelaPrincipal.setVisible(false);
        tabelaVencimentos.setVisible(true);
        diasVencimento = 7;
        tabelaVencimentos.setItems(exameService.listarExamePorVencimento(diasVencimento));
    }

    public void handleBtnMes(ActionEvent event) {
        setTabelaSecundaria();
        setLabels();
        tabelaPrincipal.setVisible(false);
        tabelaVencimentos.setVisible(true);
        diasVencimento = 30;
        tabelaVencimentos.setItems(exameService.listarExamePorVencimento(diasVencimento));
    }

    public void handleBtnSemestre(ActionEvent event) {
        setTabelaSecundaria();
        setLabels();
        tabelaPrincipal.setVisible(false);
        tabelaVencimentos.setVisible(true);
        diasVencimento = 182;
        tabelaVencimentos.setItems(exameService.listarExamePorVencimento(diasVencimento));
    }

    public void handleBtnTodos(ActionEvent event) {
        setTabelaSecundaria();
        setLabels();
        tabelaVencimentos.setVisible(true);
        tabelaPrincipal.setVisible(false);
        diasVencimento = 183;
        tabelaVencimentos.setItems(exameService.listarExamePorVencimento(diasVencimento));
    }

    public void handleBtnGeral(ActionEvent event) throws Exception {
        MainService.loadInicial();
        initialize();
        setTabelaPrincipal();
        setLabels();
        tabelaPrincipal.setItems(funcionarioService.listarFuncionarios(true));
        tabelaPrincipal.setVisible(true);
        tabelaVencimentos.setVisible(false);
        tabelaPrincipal.refresh();
    }
}
