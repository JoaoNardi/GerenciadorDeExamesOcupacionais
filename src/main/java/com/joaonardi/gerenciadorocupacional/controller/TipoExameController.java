package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.*;
import com.joaonardi.gerenciadorocupacional.service.CondicaoService;
import com.joaonardi.gerenciadorocupacional.service.SetorService;
import com.joaonardi.gerenciadorocupacional.service.TipoExameService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class TipoExameController {
    public TextField inputNome;
    public ChoiceBox<Periodicidade> inputPeriodicidade;
    public Button btnCancelar;
    public Button btnSalvar;
    public VBox containerCondicoes;
    private ObservableList<Condicao> listaDeCondicoes = FXCollections.observableArrayList();
    private Integer tipoExameId = null;

    public TableView<Condicao> tabelaCondicoes;
    public TableColumn<Condicao, String> colunaReferencia;
    public TableColumn<Condicao, String> colunaOperador;
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
        modalAddCondicao.setOpacity(0);
        inputPeriodicidade.setValue(Periodicidade.SEM_PERIODICIDADE);

        listaDeCondicoes = condicaoService.listarCondicoesPorTipoExameId(tipoExameId);
        setores = setorService.carregarSetores();
        inputPeriodicidade.getItems().addAll(Periodicidade.values());
        setTabelaCondicoes();
        listaDeCondicoes.addListener((ListChangeListener<? super Condicao>) change -> tabelaCondicoes.setItems(listaDeCondicoes));
        Platform.runLater(() -> {
            Stage stage = (Stage) btnSalvar.getScene().getWindow();
            stage.setOnCloseRequest(windowEvent -> {
                if (tipoExameId == null) {
                    return;
                } else {
                    tipoExameService.deletarTipoExame(tipoExameId);
                }
                ;
            });
        });

    }

    public void setTipoExame(TipoExame tipoExameSelecionado) {
        this.tipoExame = tipoExameSelecionado;
        if (tipoExame != null) {

            inputNome.setText(tipoExame.getNome());
            inputPeriodicidade.setValue(Periodicidade.fromValor(tipoExameSelecionado.getPeriodicidade()));
        }

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

        btnCancelarCondicao.setOnAction(event -> {
            if (!modalReferencia.getItems().isEmpty()) {
                modalReferencia.getItems().clear();
            }
            if (listaDeCondicoes.isEmpty()){
                inputPeriodicidade.setDisable(false);
            }
            modalParametro.getChildren().clear();
            modalPeriodicidade.getItems().clear();
            modalAddCondicao.setOpacity(0);
            btnAtivaModalCondicao.setOpacity(1);
            btnAtivaModalCondicao.setDisable(false);
        });
    }


    private void setTabelaCondicoes() {
        colunaReferencia.setCellValueFactory(new PropertyValueFactory<>("referencia"));
        colunaOperador.setCellValueFactory(new PropertyValueFactory<>("operador"));
        colunaOperador.setCellFactory(c -> new TableCell<Condicao, String>(){
                    @Override
                    protected void updateItem(String operador, boolean empty) {
                        super.updateItem(operador, empty);
                        if (empty || operador == null){
                            setText(null);
                        }else {
                            Operador periodo = Operador.from(operador);
                            setText(periodo.toString());

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
                    });
                    setGraphic(hBox);
                }
            }
        });

        tabelaCondicoes.setItems(listaDeCondicoes);
        listaDeCondicoes.addListener((ListChangeListener<? super Condicao>) change -> tabelaCondicoes.setItems(listaDeCondicoes));
    }


    public void cadastrarTipoExame() {
        if (inputNome.getText() == null || inputNome.getText().trim().isEmpty()) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Campo obrigatório");
            alerta.setHeaderText(null);
            alerta.setContentText("O campo Nome não pode ficar vazio.");
            alerta.showAndWait();
            return;
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
        tipoExameId = tipoExameService.cadastrarTipoExame(tipoExame).getId();
    }

    public void handleCancelarExame(ActionEvent event) {
        tipoExameService.deletarTipoExame(tipoExameId);
        janela.fecharJanela(btnCancelar);
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
                .tipoExameId(tipoExameId)
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
        cadastrarTipoExame();
        inputPeriodicidade.setDisable(true);
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

            case SETOR:
                container.getItems().addAll(Operador.IGUAL, Operador.DIFERENTE);
                break;

            case ENFERMIDADE:
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
            spinner.setPrefWidth(80);
            return spinner;
        }
        switch (referencia) {
            case Referencia.IDADE:
                Spinner<Integer> spinner = new Spinner<>(14, 100, 45); // faixa de idade
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
                return boolChoice;

            default:
                return new Label("Inválido");
        }
    }

    public void handleSalvarExame(ActionEvent event) {
        condicaoService.cadastrarListaCondicao(listaDeCondicoes);
        janela.fecharJanela(btnSalvar);
    }
}
