package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.util.Coluna;
import com.joaonardi.gerenciadorocupacional.util.Editavel;
import com.joaonardi.gerenciadorocupacional.util.FormataCPF;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class JanelaGerenciar<T> extends Janela<T> implements Editavel<T> {

    public TableView<T> tabela;
    public Button btnAdiconar;
    public Button btnEditar;
    public Button btnDeletar;
    public Janela<T> janela = new Janela<>();

    private ObservableList<T> lista;
    @FXML
    private RadioButton radioBtnAtivos;
    @FXML
    private RadioButton radioBtnInativos;
    private boolean inAtivos;
    private final ToggleGroup ativosInativos = new ToggleGroup();
    private Function<Boolean, List<T>> loader;

    private Consumer<T> delete;

    public String diretorioObjeto;
    @FXML
    public Label tituloLabel;
    private List<Coluna<T>> colunas;
    private boolean ativarRadioBtns;
    public void configurar(
            String titulo,
            String diretorioObjeto,
            ObservableList<T> lista,
            List<Coluna<T>> colunas,
            Function<Boolean, List<T>> loader,
            Consumer<T> delete,
            boolean ativarRadioBtns
    ) {
        this.diretorioObjeto = diretorioObjeto;
        this.delete = delete;
        this.lista = lista;
        this.loader = loader;
        this.colunas = colunas;
        this.ativarRadioBtns = ativarRadioBtns;
        tituloLabel.setText(titulo);

        this.lista.setAll(loader.apply(true));
        radioBtnAtivos.setDisable(!ativarRadioBtns);
        radioBtnInativos.setDisable(!ativarRadioBtns);
        radioBtnAtivos.setSelected(true);
        configurarTabela();
        configurarEventos();
    }


    public void atualizar(boolean ativos) {
        this.lista.setAll(loader.apply(ativos));
    }

    private void configurarTabela() {
        tabela.getColumns().clear();

        int index = 0;

        for (Coluna<T> col : colunas) {
            final boolean primeiraColuna = index == 0;
            final boolean ehCPF = col.nomeColuna().equalsIgnoreCase("cpf");

            TableColumn<T, String> coluna = new TableColumn<>(col.nomeColuna());

            coluna.setCellValueFactory(cell -> {
                Object valor = col.dadosColuna().apply(cell.getValue());

                if (valor == null) {
                    return new ReadOnlyStringWrapper("");
                }

                String texto = valor.toString();

                if (ehCPF) {
                    texto = FormataCPF.outPutCPF(texto);
                }

                return new ReadOnlyStringWrapper(texto);
            });

            coluna.setCellFactory(tc -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                    setAlignment(primeiraColuna ? Pos.CENTER_LEFT : Pos.CENTER);
                }
            });

            tabela.getColumns().add(coluna);
            index++;
        }

        tabela.setItems(lista);

        Platform.runLater(() -> {
            ajustarLarguraColunas();
            ajustarJanelaAoConteudo();
        });
    }



    private void ajustarLarguraColunas() {
        for (TableColumn<T, ?> coluna : tabela.getColumns()) {

            Text text = new Text(coluna.getText());
            double max = text.getLayoutBounds().getWidth();

            for (T item : tabela.getItems()) {
                Object valor = coluna.getCellData(item);
                if (valor != null) {
                    text.setText(valor.toString());
                    max = Math.max(max, text.getLayoutBounds().getWidth());
                }
            }

            double paddingSeguro = 28; // header + célula
            coluna.setPrefWidth(max + paddingSeguro);
            coluna.setMinWidth(Control.USE_PREF_SIZE);
        }
    }


    private void ajustarJanelaAoConteudo() {
        Stage stage = (Stage) tabela.getScene().getWindow();
        Screen screen = Screen.getPrimary();

        double larguraColunas = tabela.getColumns().stream()
                .mapToDouble(TableColumn::getPrefWidth)
                .sum();

        double paddingSeguro = 6;
        double decoracao = 60;

        double larguraFinal = larguraColunas + paddingSeguro + decoracao;
        double maxWidth = screen.getVisualBounds().getWidth() * 0.9;
        stage.setMinWidth(350);
        stage.setWidth(Math.min(larguraFinal, maxWidth));
        Platform.runLater(stage::centerOnScreen);
    }

    private void setBindings() {
        ativosInativos.getToggles().setAll(radioBtnAtivos, radioBtnInativos);
        ativosInativos.selectedToggleProperty().addListener((obs, old, novo) -> {
            if (novo == null) {
                old.setSelected(true);
            }
            configurarTabela();
        });

        BooleanBinding inputsValidos =
                tabela.getSelectionModel().selectedItemProperty().isNotNull();
        btnDeletar.disableProperty().bind(inputsValidos.not());
        btnEditar.disableProperty().bind(inputsValidos.not());
    }

    @FXML
    private void initialize() {
        radioBtnAtivos.setToggleGroup(ativosInativos);
        radioBtnInativos.setToggleGroup(ativosInativos);

        ativosInativos.selectedToggleProperty().addListener((obs, old, selected) -> {
            if (selected == null) return;

            inAtivos = selected == radioBtnAtivos;
            atualizar(inAtivos);
            if (this.ativarRadioBtns){
                if (radioBtnAtivos.isSelected()){
                    btnDeletar.setText("Inativar");
                }
                if (radioBtnInativos.isSelected()){
                    btnDeletar.setText("Ativar");
                }
            }
        });
        setBindings();
    }

    private void configurarEventos() {
        tabela.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tabela.getSelectionModel().isEmpty()) {
                handleEditar();
            }
        });
    }

    public void abrirSelecionado(T selecionado) {
        Stage stagePai = (Stage) tabela.getScene().getWindow();

        new Janela<T>().abrirJanela(
                diretorioObjeto,
                selecionado == null ? "Adicionar" : "Editar",
                stagePai,
                () -> atualizar(inAtivos),
                selecionado
        );
    }

    @FXML
    public void handleAdicionar() {
        abrirSelecionado(null);
    }

    @FXML
    public void handleEditar() {
        T selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            abrirSelecionado(selecionado);
        }
    }

    @FXML
    public void handleDeletar() {
        T selecionado = tabela.getSelectionModel().getSelectedItem();
        delete.accept(selecionado);
        atualizar(inAtivos);
    }
}
