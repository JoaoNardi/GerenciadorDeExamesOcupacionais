package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.*;
import com.joaonardi.gerenciadorocupacional.service.CondicaoService;
import com.joaonardi.gerenciadorocupacional.service.SetorService;
import com.joaonardi.gerenciadorocupacional.service.TipoExameService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import com.joaonardi.gerenciadorocupacional.util.TooltipUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.controlsfx.control.Notifications;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class TipoExameController {
    public TextField inputNome;
    public ChoiceBox<Periodicidade> inputPeriodicidade;
    public Button btnFechar;
    public Button btnSalvar;
    public Button btnInfo;
    private ObservableList<Condicao> listaDeCondicoes = FXCollections.observableArrayList();
    private Integer tipoExameId = null;

    public TableView<Condicao> tabelaCondicoes;
    public TableColumn<Condicao, String> colunaReferencia;
    public TableColumn colunaOperador;
    public TableColumn<Condicao, String> colunaParametro;
    public TableColumn<Condicao, String> colunaPeriodicidade;
    public TableColumn<Condicao, Node> colunaAcoes;
    //modal de adicionar condicao
    public HBox modalAddCondicao;
    public ChoiceBox<Referencia> modalReferencia;
    public ChoiceBox<Operador> modalOperador;
    public HBox modalParametro;
    public ChoiceBox<Periodicidade> modalPeriodicidade;
    public Button btnAddCondicao;
    public Button btnCancelarCondicao;
    public Button btnAtivaModalCondicao;

    SetorService setorService = new SetorService();
    ObservableList<Setor> setores = null;
    CondicaoService condicaoService = new CondicaoService();

    Janela janela = new Janela();
    private TipoExame tipoExame;
    TipoExameService tipoExameService = new TipoExameService();

    public void initialize() {
        setorService.carregarSetores();
        FontIcon iconInfo = new FontIcon(FontAwesomeSolid.INFO);
        Tooltip tooltip = new Tooltip("A Condição torna o tipo de exame obrigatório o funcionario que contemple a regra");
        btnInfo.setGraphic(iconInfo);
        btnInfo.setTooltip(tooltip);
        TooltipUtils.installWithDelay(btnInfo,tooltip,200);
        modalAddCondicao.setOpacity(0);
        inputPeriodicidade.setValue(Periodicidade.SEM_PERIODICIDADE);
        setores = setorService.listarSetores();
        inputPeriodicidade.getItems().addAll(Periodicidade.values());
        setTabelaCondicoes();
        listaDeCondicoes.addListener((ListChangeListener<? super Condicao>) change -> tabelaCondicoes.setItems(listaDeCondicoes));
        Platform.runLater(() -> {
            Stage stage = (Stage) btnSalvar.getScene().getWindow();
            stage.setOnCloseRequest(windowEvent -> {
                if (tipoExameId != null) {
                    tipoExameService.deletarTipoExame(tipoExameId);
                }
            });
        });

    }

    public void setTipoExame(TipoExame tipoExameSelecionado) {
        this.tipoExame = tipoExameSelecionado;
        if (tipoExame != null) {

            inputNome.setText(tipoExame.getNome());
            inputPeriodicidade.setValue(Periodicidade.fromValor(tipoExameSelecionado.getPeriodicidade()));
        }
        listaDeCondicoes = condicaoService.listarCondicoesPorTipoExameId(tipoExameSelecionado.getId());
        setTabelaCondicoes();
    }

    private void setModalCondicao() {
        FontIcon iconeAdd = new FontIcon(FontAwesomeSolid.FOLDER_PLUS);
        btnAddCondicao.setGraphic(iconeAdd);
        btnAddCondicao.setOnAction(event -> criaCondicao());

        FontIcon iconeRemover = new FontIcon(FontAwesomeSolid.TRASH);
        btnCancelarCondicao.setGraphic(iconeRemover);

        modalReferencia.getItems().clear();
        modalReferencia.getItems().addAll(Referencia.values());
        modalReferencia.setValue(Referencia.IDADE); // valor inicial

        operadorChoiceBoxPorReferencia(modalReferencia.getValue(), modalOperador);
        modalReferencia.valueProperty().addListener((obs, oldVal, newVal) -> {
            operadorChoiceBoxPorReferencia(newVal, modalOperador);
        });

        modalParametro.getChildren().setAll(criarParametroNode(modalReferencia.getValue()));
        modalReferencia.valueProperty().addListener((obs, oldVal, newVal) -> {
            modalParametro.getChildren().setAll(criarParametroNode(modalReferencia.getValue()));
        });

        modalPeriodicidade.getItems().clear();
        modalPeriodicidade.getItems().addAll(Periodicidade.values());
        modalPeriodicidade.setValue(Periodicidade.SEM_PERIODICIDADE);

        btnCancelarCondicao.setOnAction(event -> {
            actionFecharModalCondicao();
        });
    }

    private void actionFecharModalCondicao() {
        if (!modalReferencia.getItems().isEmpty()) {
            modalReferencia.getItems().clear();
        }
        if (listaDeCondicoes.isEmpty()) {
            inputPeriodicidade.setDisable(false);
        }
        modalParametro.getChildren().clear();
        modalPeriodicidade.getItems().clear();
        modalAddCondicao.setOpacity(0);
        btnAtivaModalCondicao.setOpacity(1);
        btnAtivaModalCondicao.setDisable(false);
    }


    private void setTabelaCondicoes() {
        colunaReferencia.setCellValueFactory(new PropertyValueFactory<>("referencia"));
        colunaOperador.setCellValueFactory(new PropertyValueFactory<>("operador"));
        colunaOperador.setCellFactory(c -> new TableCell<Condicao, String>() {
                    @Override
                    protected void updateItem(String operador, boolean empty) {
                        super.updateItem(operador, empty);
                        if (empty || operador == null) {
                            setText(null);
                        } else {
                            Operador caracterOperador = Operador.from(operador);
                            setText(String.valueOf(caracterOperador));

                        }
                    }
                }
        );
        colunaParametro.setCellValueFactory(new PropertyValueFactory<>("parametro"));
        colunaPeriodicidade.setCellValueFactory(new PropertyValueFactory<>("periodicidade"));
        colunaAcoes.setCellFactory(coluna -> new TableCell<>() {
            FontIcon iconeRemover = new FontIcon(FontAwesomeSolid.TRASH);
            Button btnRemover = new Button();

            private final HBox hBox = new HBox(10, btnRemover);

            @Override
            protected void updateItem(Node node, boolean b) {
                super.updateItem(node, b);
                if (b) {
                    setGraphic(null);
                } else {
                    btnRemover.setGraphic(iconeRemover);
                    btnRemover.setOnAction(e -> {
                        int index = getTableRow().getIndex();
                        listaDeCondicoes.remove(index);
                        if (getTableRow().getItem() != null && getTableRow().getItem().getId() != null) {
                            condicaoService.deletarCondicao(getTableRow().getItem());
                        }
                    });
                    setGraphic(hBox);
                }
            }
        });

        tabelaCondicoes.setItems(listaDeCondicoes);
        listaDeCondicoes.addListener((ListChangeListener<? super Condicao>) change -> tabelaCondicoes.setItems(listaDeCondicoes));
    }

    public TipoExame cadastrarTipoExame() {
        if (inputNome == null || inputNome.getText().isEmpty()) {
            Notifications.create()
                    .title("Erro")
                    .text("Nome Invalido")
                    .hideAfter(Duration.seconds(3))
                    .owner(janela.stage)
                    .showError();
            return null;
        }
        if (tipoExame == null || tipoExame.getId() == null) {
            tipoExame = TipoExame.TipoExameBuilder.builder()
                    .id(null)
                    .nome(inputNome.getText())
                    .periodicidade(inputPeriodicidade.getValue().getValor())
                    .build();
        } else {
            tipoExame = TipoExame.TipoExameBuilder.builder()
                    .id(this.tipoExame.getId())
                    .nome(inputNome.getText())
                    .periodicidade(inputPeriodicidade.getValue().getValor())
                    .build();
        }
        try {
            tipoExame = tipoExameService.cadastrarTipoExame(tipoExame);
            Notifications.create()
                    .title("Sucesso")
                    .text("Tipo de Exame salvo com sucesso")
                    .hideAfter(Duration.seconds(3))
                    .owner(janela.stage)
                    .showConfirm();
            return tipoExame;
        } catch (RuntimeException e) {
            Notifications.create()
                    .title("Erro")
                    .text("Tipo de Exame já cadastrado")
                    .hideAfter(Duration.seconds(3))
                    .owner(janela.stage)
                    .showConfirm();
        }
        return null;
    }

    public void handleCancelarExame(ActionEvent event) {
        janela.fecharJanela(btnFechar);
    }

    public Condicao criaCondicao() {
        String parametro = null;
        if (modalParametro.getChildren().getFirst() instanceof Spinner<?>)
            parametro = ((Spinner<?>) modalParametro.getChildren().getFirst()).getValue().toString();
        if (modalParametro.getChildren().getFirst() instanceof ChoiceBox<?>)
            parametro = ((ChoiceBox<?>) modalParametro.getChildren().getFirst()).getValue().toString();
        Condicao condicao = null;
        condicao = Condicao.CondicaoBuilder.builder()
                .id(null)
                .tipoExameId(tipoExame.getId())
                .referencia(String.valueOf(modalReferencia.getValue()))
                .operador(String.valueOf(modalOperador.getValue().getOperador()))
                .parametro(parametro)
                .periodicidade(modalPeriodicidade.getValue().getValor())
                .build();
        listaDeCondicoes.add(condicao);
        return condicao;
    }

    @FXML
    public void handleAdicionarCondicoes(ActionEvent event) {
        if (inputNome == null || inputNome.getText().isEmpty()) {
            Notifications.create()
                    .title("Erro")
                    .text("Nome Invalido")
                    .hideAfter(Duration.seconds(3))
                    .owner(janela.stage)
                    .showError();
            return;
        }
        if (cadastrarTipoExame() == null){
            inputNome.clear();
            return;
        };
        modalAddCondicao.setOpacity(1);
        btnAtivaModalCondicao.setOpacity(0);
        btnAtivaModalCondicao.setDisable(true);
        setModalCondicao();
    }

    private void operadorChoiceBoxPorReferencia(Referencia referencia, ChoiceBox<Operador> container) {
        container.getItems().clear();
        switch (referencia) {
            case IDADE:
                container.getItems().addAll(Operador.values());
                break;
            case SETOR, ENFERMIDADE:
                container.getItems().addAll(Operador.IGUAL, Operador.DIFERENTE);
                break;
            case null:
                break;
        }

        if (!container.getItems().isEmpty()) {
            container.setValue(container.getItems().get(0)); // valor padrao
        }
    }

    private Node criarParametroNode(Referencia referencia) {
        if (referencia == null) {
            Spinner<Integer> spinner = new Spinner<>(14, 100, 45); // faixa de idade
            spinner.setEditable(true);
            spinner.setPrefWidth(80);
            return spinner;
        }
        switch (referencia) {
            case Referencia.IDADE:
                Spinner<Integer> spinner = new Spinner<>(14, 100, 45); // faixa de idade
                spinner.setEditable(true);
                spinner.setPrefWidth(80);
                return spinner;

            case Referencia.SETOR:
                ChoiceBox<Setor> setorChoice = new ChoiceBox<>();
                setorChoice.setItems(setores);
                setorChoice.setPrefWidth(120);
                setorChoice.setValue(setores.get(0));
                return setorChoice;

            case Referencia.ENFERMIDADE:
                ChoiceBox<Boolean> boolChoice = new ChoiceBox<>();
                boolChoice.getItems().addAll(true, false);
                boolChoice.setValue(false);
                boolChoice.setConverter(new StringConverter<Boolean>() {
                    @Override
                    public String toString(Boolean object) {
                        if (object == null) return "";
                        return object ? "Verdadeiro" : "Falso";
                    }

                    @Override
                    public Boolean fromString(String string) {
                        if (string.equalsIgnoreCase("Verdadeiro")) return true;
                        if (string.equalsIgnoreCase("Falso")) return false;
                        return null;
                    }
                });
                return boolChoice;

            default:
                return new Label("Inválido");
        }
    }

    public void handleSalvarExame(ActionEvent event) {
        cadastrarTipoExame();
        condicaoService.cadastrarListaCondicao(listaDeCondicoes);

    }
}
