package com.joaonardi.gerenciadorocupacional.controller;


import com.joaonardi.gerenciadorocupacional.util.Coluna;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class JanelaGerenciar<T> extends Janela {

    public TableView<T> tabela;
    public Button btnAdiconar;
    public Button btnEditar;
    public Button btnDeletar;
    public Janela janela = new Janela();

    public ObservableList<T> lista;
    private Supplier<List<T>> loader;
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
            Supplier<List<T>> loader,
            Consumer<T> delete
    ) {
        this.diretorioObjeto = diretorioObjeto;
        this.delete = delete;
        this.lista = lista;
        this.loader = loader;
        this.lista.setAll(loader.get());
        this.colunas = colunas;
        tituloLabel.setText(titulo);
        configurarTabela();
        configurarEventos();
    }

    public void atualizar() {
        lista.setAll(loader.get());
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
        BooleanBinding inputsValidos =
                tabela.getSelectionModel().selectedItemProperty().isNotNull();
        btnDeletar.disableProperty().bind(inputsValidos.not());
        btnEditar.disableProperty().bind(inputsValidos.not());
    }

    @FXML
    private void initialize() {
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
        Janela janelaEditor = new Janela();
        Stage stage1 = (Stage) tabela.getScene().getWindow();
        System.out.println(stage1);
        janelaEditor.abrirJanela(
                diretorioObjeto,
                selecionado == null ? "Adicionar" : "Editar",
                stage1,
                this::atualizar
        );
        if (selecionado != null) {
            Janela controller = janelaEditor.loader.getController();
            controller.set(selecionado);
        }
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
        atualizar();
    }
}
