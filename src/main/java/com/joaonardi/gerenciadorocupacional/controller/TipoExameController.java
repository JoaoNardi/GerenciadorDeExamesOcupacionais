package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.*;
import com.joaonardi.gerenciadorocupacional.service.CondicaoService;
import com.joaonardi.gerenciadorocupacional.service.ConjuntoService;
import com.joaonardi.gerenciadorocupacional.service.SetorService;
import com.joaonardi.gerenciadorocupacional.service.TipoExameService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import com.joaonardi.gerenciadorocupacional.util.TooltipUtils;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.controlsfx.control.Notifications;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.swing.*;

public class TipoExameController {
    public TextField inputNome;
    public Button btnFechar;
    public Button btnSalvar;
    public Button btnInfo;

    public VBox boxPeriodicidade;
    public TableView<Conjunto> tabelaConjuntos;
    public TableColumn<Conjunto, String> colunaConjuntosRegras;
    public TableColumn<?, Node> colunaAcoesRegras;
    public Button btnAddRegra;
    private ObjectProperty<Conjunto> conjuntoSelecionado = new SimpleObjectProperty<>(null);

    public ChoiceBox<Periodicidade> inputPeridicidade;

    public TableView<Condicao> tabelaCondicoes;
    public TableColumn<Condicao, String> colunaReferencia;
    public TableColumn<Condicao, String> colunaOperador;
    public TableColumn<Condicao, String> colunaParametro;
    public TableColumn<?, Node> colunaAcoes;
    //modal de adicionar condicao
    public HBox modalAddCondicao;
    public ChoiceBox<Referencia> modalReferencia;
    public ChoiceBox<Operador> modalOperador;
    public HBox modalParametro;
    public Button btnAddCondicao;
    public Button btnCancelarCondicao;
    public Button btnAtivaModalCondicao;
    final ConjuntoService conjuntoService = new ConjuntoService();
    final SetorService setorService = new SetorService();
    ObservableList<Setor> setores = null;
    final CondicaoService condicaoService = new CondicaoService();

    final Janela janela = new Janela();
    private TipoExame tipoExame;
    final TipoExameService tipoExameService = new TipoExameService();

    public void initialize() {
        FontIcon iconInfo = new FontIcon(FontAwesomeSolid.INFO);
        Tooltip tooltip = new Tooltip("A Condição torna o tipo de exame obrigatório o funcionario que contemple a regra");
        btnInfo.setGraphic(iconInfo);
        btnInfo.setTooltip(tooltip);
        TooltipUtils.installWithDelay(btnInfo, tooltip, 200);
        conjuntoSelecionado.addListener((obs, oldV, newV) -> {
            atualizarTabelaCondicoes();
            btnAtivaModalCondicao.setOpacity(newV != null ? 1 : 0);
        });

        setorService.carregarSetores();
        setores = setorService.listarSetores();
        setores.add(null);
        inputPeridicidade.getItems().addAll(Periodicidade.values());
        conjuntoService.listarConjuntos().clear();
        condicaoService.listarCondicoes().addListener(
                (ListChangeListener<Condicao>) change -> {
                    if (conjuntoSelecionado.getValue() != null) {
                        atualizarTabelaCondicoes();
                    }
                }
        );
        inputNome.focusedProperty().addListener((obs, oldValue, newValue) -> {
            cadastrarTipoExame();
        });
        setTabelaConjuntos();
    }

    public void setTipoExame(TipoExame tipoExameSelecionado) {
        this.tipoExame = tipoExameSelecionado;
        if (tipoExame != null) {
            inputNome.setText(tipoExame.getNome());
        }
        if (tipoExameSelecionado != null) {
            conjuntoService.carregarConjuntoTipoExameId(tipoExameSelecionado.getId());
        }
        setTabelaCondicoes();
        setTabelaConjuntos();
    }

    private void setTabelaConjuntos() {
        colunaConjuntosRegras.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getPeriodicidade() + " Meses"));
        setColunaAcoesGeneric((TableColumn<Object, Node>) colunaAcoesRegras);

        tabelaConjuntos.setItems(conjuntoService.listarConjuntos());
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
        modalReferencia.valueProperty().addListener((obs, oldVal, newVal) -> operadorChoiceBoxPorReferencia(newVal, modalOperador));

        modalParametro.getChildren().setAll(criarParametroNode(modalReferencia.getValue()));
        modalReferencia.valueProperty().addListener((obs, oldVal, newVal) -> modalParametro.getChildren().setAll(criarParametroNode(modalReferencia.getValue())));
        btnCancelarCondicao.setOnAction(event -> actionFecharModalCondicao());
    }

    private void actionFecharModalCondicao() {
        if (!modalReferencia.getItems().isEmpty()) {
            modalReferencia.getItems().clear();
        }
        modalParametro.getChildren().clear();
        modalAddCondicao.setOpacity(0);
        btnAtivaModalCondicao.setOpacity(1);
        btnAtivaModalCondicao.setDisable(false);
    }


    private void setTabelaCondicoes() {
        colunaReferencia.setCellValueFactory(new PropertyValueFactory<>("referencia"));
        colunaOperador.setCellValueFactory(new PropertyValueFactory<>("operador"));
        colunaOperador.setCellFactory(c -> new TableCell<>() {
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
        setColunaAcoesGeneric((TableColumn<Object, Node>) colunaAcoes);
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
                    .build();
        } else {
            tipoExame = TipoExame.TipoExameBuilder.builder()
                    .id(this.tipoExame.getId())
                    .nome(inputNome.getText())
                    .build();
        }
        try {
            setTipoExame(tipoExameService.cadastrarTipoExame(tipoExame));
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
                    .text("Tipo de Exame já cadastrado" + e.getMessage())
                    .hideAfter(Duration.seconds(3))
                    .owner(janela.stage)
                    .showConfirm();
        }
        return null;
    }

    public void handleCancelarExame() {
        janela.fecharJanela(btnFechar);
    }

    public void criaCondicao() {
        String parametro = null;
        if (modalParametro.getChildren().getFirst() instanceof Spinner<?>)
            parametro = ((Spinner<?>) modalParametro.getChildren().getFirst()).getValue().toString();
        if (modalParametro.getChildren().getFirst() instanceof ChoiceBox<?>)
            parametro = ((ChoiceBox<?>) modalParametro.getChildren().getFirst()).getValue().toString();
        Condicao condicao = Condicao.CondicaoBuilder.builder()
                .id(null)
                .conjuntoId(conjuntoSelecionado.getValue().getId())
                .referencia(String.valueOf(modalReferencia.getValue()))
                .operador(String.valueOf(modalOperador.getValue().getOperador()))
                .parametro(parametro)
                .build();
        condicaoService.listarCondicoes().addAll(condicao);

    }

    @FXML
    public void handleAdicionarCondicoes() {
        if (inputNome == null || inputNome.getText().isEmpty()) {
            Notifications.create()
                    .title("Erro")
                    .text("Nome Invalido")
                    .hideAfter(Duration.seconds(3))
                    .owner(janela.stage)
                    .showError();
            return;
        }
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
            container.setValue(container.getItems().getFirst()); // valor padrao
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
                setorChoice.setValue(setores.getFirst());
                return setorChoice;

            case Referencia.ENFERMIDADE:
                ChoiceBox<Boolean> boolChoice = new ChoiceBox<>();
                boolChoice.getItems().addAll(true, false);
                boolChoice.setValue(false);
                boolChoice.setConverter(new StringConverter<>() {
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

    public void setColunaAcoesGeneric(TableColumn<Object, Node> colunaAcao) {
        colunaAcao.setCellFactory(coluna -> new TableCell<>() {
            final FontIcon iconeRemover = new FontIcon(FontAwesomeSolid.TRASH);
            final Button btnRemover = new Button();

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
                        if (getTableRow().getItem() instanceof Condicao) {
                            acaoRemmoverColuna(index, condicaoService.listarCondicoes(), getTableRow());
                        }
                        if (getTableRow().getItem() instanceof Conjunto) {
                            acaoRemmoverColuna(index, conjuntoService.listarConjuntos(), getTableRow());
                        }
                    });
                    setGraphic(hBox);
                }
            }
        });
    }

    public void acaoRemmoverColuna(int index, ObservableList<?> lista, TableRow<?> tableRow) {
        try {
            if (tableRow.getItem() instanceof Condicao) {
                if (tableRow.getItem() != null && ((Condicao) tableRow.getItem()).getId() != null) {
                    condicaoService.deletarCondicao((Condicao) tableRow.getItem());
                }
            }
            if (tableRow.getItem() instanceof Conjunto) {
                if (tableRow.getItem() != null && ((Conjunto) tableRow.getItem()).getId() != null) {
                    conjuntoService.deletarConjunto((Conjunto) tableRow.getItem());
                }
            }
            lista.remove(index);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void handleSalvarExame() {
        cadastrarTipoExame();
        condicaoService.cadastrarListaCondicao(condicaoService.listarCondicoes());
        janela.fecharJanela(btnSalvar);
    }

    public void handleAdicionarRegra() {
        modalAddRegraSwitch(true);
    }

    private void modalAddRegraSwitch(boolean inChoicebox) {
        inputPeridicidade.setDisable(!inChoicebox);
        inputPeridicidade.setOpacity(inChoicebox ? 1 : 0);
        btnAddRegra.setDisable(inChoicebox);
        btnAddRegra.setOpacity(inChoicebox ? 0 : 1);
    }

    //
    public void handleSelecionarConjunto() {
        conjuntoSelecionado.setValue(tabelaConjuntos.selectionModelProperty().getValue().getSelectedItem());
        condicaoService.carregarCondicoesPorConjuntoId(conjuntoSelecionado.get().getId());
        atualizarTabelaCondicoes();
    }

    private void atualizarTabelaCondicoes() {
        tabelaCondicoes.setItems(FXCollections.observableArrayList(condicaoService.listarCondicoes()));
    }

    public void handleAddPeriodicidade(ActionEvent event) {
        Conjunto conjuntoNovo = Conjunto.ConjuntoBuilder.builder()
                .id(null)
                .tipoExameId(tipoExame.getId())
                .periodicidade(inputPeridicidade.getValue().getValor())
                .build();
        conjuntoSelecionado.setValue(conjuntoNovo);
        conjuntoService.cadastrarConjunto(conjuntoNovo);
        Platform.runLater(() -> {
            tabelaConjuntos.getSelectionModel().select(tabelaConjuntos.getItems().size() - 1);
            tabelaConjuntos.scrollTo(tabelaConjuntos.getItems().size() - 1);
        });
        tabelaConjuntos.setItems(conjuntoService.listarConjuntos());
        modalAddRegraSwitch(false);
        setTabelaConjuntos();
    }

}
