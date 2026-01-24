package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.*;
import com.joaonardi.gerenciadorocupacional.service.CondicaoService;
import com.joaonardi.gerenciadorocupacional.service.ConjuntoService;
import com.joaonardi.gerenciadorocupacional.service.SetorService;
import com.joaonardi.gerenciadorocupacional.service.TipoExameService;
import com.joaonardi.gerenciadorocupacional.util.ComboBoxCustom;
import com.joaonardi.gerenciadorocupacional.util.Editavel;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import com.joaonardi.gerenciadorocupacional.util.TooltipUtils;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.controlsfx.control.Notifications;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class TipoExameController extends Janela<TipoExame> implements Editavel<TipoExame> {
    public TextField inputNome;
    public Button btnFechar;
    public Button btnSalvar;
    public Button btnInfo;

    public VBox boxPeriodicidade;
    public TableView<Conjunto> tabelaConjuntos;
    public TableColumn<Conjunto, String> colunaConjuntosRegras;
    public TableColumn<?, Node> colunaAcoesRegras;
    public Button btnAddRegra;
    private final ObjectProperty<Conjunto> conjuntoSelecionado = new SimpleObjectProperty<>(null);

    public ChoiceBox<Periodicidade> inputPeriodicidade;

    private final ObjectProperty<Node> parametroAtual = new SimpleObjectProperty<>();

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
    final CondicaoService condicaoService = new CondicaoService();

    private final ObservableList<Setor> setores = FXCollections.observableArrayList();

    final Janela<TipoExame> janela = new Janela<>();
    private TipoExame tipoExame;
    final TipoExameService tipoExameService = new TipoExameService();


    public void initialize() {
        FontIcon iconInfo = new FontIcon(FontAwesomeSolid.INFO);

        btnInfo.setGraphic(iconInfo);
        btnInfo.setOnAction(event -> JOptionPane.showMessageDialog(
                null,
                """
                A Condição torna o tipo de exame obrigatório o funcionario que contemple a regra\s
                 \
                OBS: Será atendida atendindo o conjunto de regras que houver maior compatibilidade com o funcionário!\s
                 \
                (Em caso de conflito, será aplicada a menor periodicidade)""",
                "Infomativo",
                JOptionPane.INFORMATION_MESSAGE
        ));
        conjuntoSelecionado.addListener((obs, oldV, newV) -> {
            atualizarTabelaCondicoes();
            btnAtivaModalCondicao.setOpacity(newV != null ? 1 : 0);
            btnAtivaModalCondicao.setDisable(newV == null);
        });
        btnAtivaModalCondicao.setDisable(true);
        setores.setAll(setorService.listarSetores());
        inputPeriodicidade.getItems().addAll(Periodicidade.values());
        condicaoService.listarCondicoes().clear();
        condicaoService.listarCondicoes().addListener(
                (ListChangeListener<Condicao>) change -> {
                    if (conjuntoSelecionado.getValue() != null) {
                        atualizarTabelaCondicoes();
                    }
                }
        );


        inputNome.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                return;
            }
            cadastrarTipoExame();
        });

        Platform.runLater(()->{
            if (this.tipoExame != null){
                setTabelaConjuntos();
            }
            setBindings();
        });
    }

    private void setBindings() {
        BooleanBinding inputsValidos =
                inputNome.textProperty().isNotNull()
                        .and(tabelaCondicoes.itemsProperty().isNotNull());
        btnSalvar.disableProperty().bind(inputsValidos.not());

        BooleanBinding periodicidadeSelecionada = conjuntoSelecionado.isNotNull();
        btnAddCondicao.disableProperty().bind(periodicidadeSelecionada);

    }

    @Override
    public void set(TipoExame objeto) {
        super.set(objeto);
        Platform.runLater(()-> {
            this.tipoExame = objeto;
            if (tipoExame != null) {
                inputNome.setText(tipoExame.getNome());
            }
            setTabelaCondicoes();
            setTabelaConjuntos();
        });
    }

    private void setTabelaConjuntos() {
        colunaConjuntosRegras.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getPeriodicidade() + " Meses"));
        setColunaAcoesGeneric((TableColumn<Object, Node>) colunaAcoesRegras);
        Platform.runLater(()-> {tabelaConjuntos.setItems(conjuntoService.listarConjuntos(this.tipoExame.getId()));});
    }

    private void setModalCondicao() {
        FontIcon iconeAdd = new FontIcon(FontAwesomeSolid.FOLDER_PLUS);
        btnAddCondicao.setGraphic(iconeAdd);
        btnAddCondicao.setOnAction(event -> criaCondicao());

        FontIcon iconeRemover = new FontIcon(FontAwesomeSolid.WINDOW_CLOSE);
        btnCancelarCondicao.setGraphic(iconeRemover);

        modalReferencia.getItems().clear();
        modalReferencia.getItems().addAll(Referencia.values());
        modalReferencia.setValue(Referencia.IDADE);

        operadorChoiceBoxPorReferencia(modalReferencia.getValue(), modalOperador);
        modalReferencia.valueProperty().addListener((obs, oldVal, newVal) -> operadorChoiceBoxPorReferencia(newVal, modalOperador));

        trocarParametro(criarParametroNode(modalReferencia.getValue()));
        inicializarCondicao();
        modalReferencia.valueProperty().addListener((obs, oldVal, newVal) -> trocarParametro(criarParametroNode(modalReferencia.getValue())));
        btnCancelarCondicao.setOnAction(event -> actionFecharModalCondicao());
    }

    private void trocarParametro(Node novoParametro) {
        modalParametro.getChildren().setAll(novoParametro);
        parametroAtual.set(novoParametro);
    }

    private void actionFecharModalCondicao() {
        if (!modalReferencia.getItems().isEmpty()) {
            modalReferencia.getItems().clear();
        }
        modalParametro.getChildren().clear();
        modalAddCondicao.setOpacity(0);
        btnAtivaModalCondicao.setOpacity(1);
        btnAtivaModalCondicao.setDisable(false);
        modalAddRegraSwitch(false);
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
            set(tipoExameService.cadastrarTipoExame(tipoExame));
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
        Node parametroNode = modalParametro.getChildren().getFirst();
        String parametro = extrairParametro(parametroNode);
        if (parametro == null) {
            return;
        }
        Condicao condicao = Condicao.CondicaoBuilder.builder()
                .id(null)
                .conjunto(conjuntoSelecionado.getValue())
                .referencia(String.valueOf(modalReferencia.getValue()))
                .operador(String.valueOf(modalOperador.getValue().getOperador()))
                .parametro(parametro)
                .build();

        condicaoService.cadastrarCondicao(condicao);
        tabelaCondicoes.refresh();
        actionFecharModalCondicao();
    }

    private String extrairParametro(Node node) {
        if (node instanceof Spinner<?> spinner)
            return String.valueOf(spinner.getValue());

        if (node instanceof ChoiceBox<?> choiceBox)
            return choiceBox.getValue() != null
                    ? choiceBox.getValue().toString()
                    : null;

        if (node instanceof ComboBoxCustom<?> combo)
            return combo.getValue() != null
                    ? combo.getValue().toString()
                    : null;

        return null;
    }

    private BooleanBinding bindingParametroValido(Node node) {
        if (node instanceof Spinner<?> spinner)
            return spinner.valueProperty().isNotNull();

        if (node instanceof ChoiceBox<?> choiceBox)
            return choiceBox.valueProperty().isNotNull();

        if (node instanceof ComboBoxCustom<?> combo)
            return combo.valueProperty().isNotNull();

        return new SimpleBooleanProperty(false).not();
    }

    public void inicializarCondicao() {
        BooleanBinding parametroValido = new BooleanBinding() {
            {
                bind(parametroAtual);
            }

            @Override
            protected boolean computeValue() {
                Node node = parametroAtual.get();
                if (node == null) return false;

                return bindingParametroValido(node).get();
            }
        };
        btnAddCondicao.disableProperty().bind(parametroValido.not());
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
        btnAddRegra.setDisable(true);
        inputPeriodicidade.setDisable(true);
        btnAddRegra.setOpacity(0);
        inputPeriodicidade.setOpacity(0);
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
                ComboBoxCustom<Setor> setorChoice = new ComboBoxCustom<>();
                setorChoice.setItemsAndDisplay(setores, List.of(Setor::getArea));
                setorChoice.setPrefWidth(120);
                setorChoice.setValue(setorChoice.getItems().getFirst());
                return setorChoice;

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
                            acaoRemmoverColuna(index, conjuntoService.listarConjuntos(tipoExame.getId()), getTableRow());
                            tabelaConjuntos.setItems(conjuntoService.listarConjuntos(tipoExame.getId()));
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
        janela.fecharJanela(btnSalvar);
    }

    public void handleAdicionarRegra() {
        modalAddRegraSwitch(true);
    }

    private void modalAddRegraSwitch(boolean inChoicebox) {
        switchBtn(inputPeriodicidade,!inChoicebox);
        switchBtn(btnAddRegra,inChoicebox);
    }

    private void switchBtn(Node node, boolean setDisable){
        node.setDisable(setDisable);
        node.setOpacity(setDisable ? 0 : 1);
    }

    public void handleSelecionarConjunto() {
        conjuntoSelecionado.setValue(tabelaConjuntos.selectionModelProperty().getValue().getSelectedItem());
        condicaoService.carregarCondicoesPorConjuntoId(conjuntoSelecionado.get().getId());
        atualizarTabelaCondicoes();
    }

    private void atualizarTabelaCondicoes() {
        if (conjuntoSelecionado.getValue().getId() == null){
            return;
        }
        tabelaCondicoes.setItems(FXCollections.observableArrayList(condicaoService.listarCondicoes()));
    }

    public void handleAddPeriodicidade() {

        var periodicidadeSelecionada = inputPeriodicidade.getValue();

        if (periodicidadeSelecionada == null) {
            return;
        }
        Conjunto conjuntoNovo = Conjunto.ConjuntoBuilder.builder()
                .id(null)
                .tipoExame(tipoExame)
                .periodicidade(periodicidadeSelecionada.getValor())
                .build();
        conjuntoSelecionado.setValue(conjuntoNovo);
        conjuntoService.cadastrarConjunto(conjuntoNovo);

        tabelaConjuntos.setItems(
                conjuntoService.listarConjuntos(tipoExame.getId())
        );

        tabelaConjuntos.getSelectionModel()
                .select(tabelaConjuntos.getItems().size() - 1);
        tabelaConjuntos.scrollTo(
                tabelaConjuntos.getItems().size() - 1
        );
        Platform.runLater(() -> {
            handleSelecionarConjunto();
            inputPeriodicidade.getSelectionModel().clearSelection();
            modalAddRegraSwitch(false);
        });

        setTabelaConjuntos();
        setTabelaCondicoes();

    }


}
