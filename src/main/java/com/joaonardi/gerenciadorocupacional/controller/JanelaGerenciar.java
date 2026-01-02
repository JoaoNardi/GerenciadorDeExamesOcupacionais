package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.util.Coluna;
import com.joaonardi.gerenciadorocupacional.util.Editavel;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

        for (Coluna<T> col : colunas) {
            TableColumn<T, String> coluna = new TableColumn<>(col.nomeColuna());

            coluna.setCellValueFactory(cell -> {
                Object valor = col.dadosColuna().apply(cell.getValue());
                return new ReadOnlyStringWrapper(valor != null ? valor.toString() : "");
            });
            tabela.getColumns().add(coluna);
        }
        tabela.setItems(lista);
    }

    private void setBindings() {
        ativosInativos.getToggles().setAll(radioBtnAtivos, radioBtnInativos);
        ativosInativos.selectedToggleProperty().addListener((obs, old, novo) -> {
            if (novo == null) {
                old.setSelected(true);
            }
        });

        BooleanBinding inputsValidos =
                tabela.getSelectionModel().selectedItemProperty().isNotNull();
        btnDeletar.disableProperty().bind(inputsValidos.not());
        btnEditar.disableProperty().bind(inputsValidos.not());
    }

    @FXML
    private void initialize() {
        //disabilitar ativosinativos
        radioBtnAtivos.setToggleGroup(ativosInativos);
        radioBtnInativos.setToggleGroup(ativosInativos);

        ativosInativos.selectedToggleProperty().addListener((obs, old, selected) -> {
            if (selected == null) return;

            inAtivos = selected == radioBtnAtivos;
            atualizar(inAtivos);
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
