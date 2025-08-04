package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.*;
import com.joaonardi.gerenciadorocupacional.service.SetorService;
import com.joaonardi.gerenciadorocupacional.service.TipoExameService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class TipoExameController {
    public TextField inputNome;
    public ChoiceBox<Periodicidade> inputPeriodicidade;
    public Button btnCancelar;
    public Button btnSalvar;
    public VBox containerCondicoes;
    public ScrollPane scrollPaneCondicoes;
    SetorService setorService = new SetorService();
    ObservableList<Setor> setores = null;

    Janela janela = new Janela();
    private TipoExame tipoExame;
    TipoExameService tipoExameService = new TipoExameService();

    public void initialize() {
        scrollPaneCondicoes.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        setores = setorService.carregarSetores();
        inputPeriodicidade.getItems().addAll(Periodicidade.values());
    }

    public void setTipoExame(TipoExame tipoExameSelecionado) {
        this.tipoExame = tipoExameSelecionado;
        if (tipoExame !=null){

            inputNome.setText(tipoExame.getNome());
            inputPeriodicidade.setValue(Periodicidade.fromValor(tipoExameSelecionado.getPeriodicidade()));
        }

    }

    @FXML
    public void handleSalvarExame(ActionEvent event) {
        if (tipoExame == null || tipoExame.getId() ==null){
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
        tipoExameService.cadastrarTipoExame(tipoExame);
        janela.fecharJanela(btnSalvar);
    }

    public void handleCancelarExame(ActionEvent event) {
        janela.fecharJanela(btnCancelar);
    }

    @FXML
    public void handleAdicionarCondicoes(ActionEvent event) {
        HBox linha = new HBox(10);

        Pane operadorContainer = new Pane();
        Node operadorNode = criarOperadorNode(Referencia.IDADE,operadorContainer);

        ChoiceBox<Referencia> referenciaChoiceBox = new ChoiceBox<>();
        referenciaChoiceBox.getItems().addAll(Referencia.values());
        referenciaChoiceBox.setValue(Referencia.IDADE); // Prompt inicial

        Pane parametroContainer = new Pane();
        Node parametroNode = criarParametroNode(Referencia.IDADE, parametroContainer); // Prompt inicial

        ChoiceBox<Periodicidade> periodicidade = new ChoiceBox<>();
        periodicidade.getItems().addAll(Periodicidade.values());
        periodicidade.setValue(Periodicidade.SEM_PERIODICIDADE);

        FontIcon iconeRemover = new FontIcon(FontAwesomeSolid.TRASH);
        Button remover = new Button();
        remover.setGraphic(iconeRemover);
        remover.setOnAction(e -> containerCondicoes.getChildren().remove(linha));

        referenciaChoiceBox.setOnAction(e -> {
            Referencia ref = referenciaChoiceBox.getValue();
            Node novoParametro = criarParametroNode(ref, parametroContainer);
            parametroContainer.getChildren().setAll(novoParametro);


            Node novoOperador = criarOperadorNode(ref, operadorContainer);
            operadorContainer.getChildren().setAll(novoOperador);
        });

        parametroContainer.getChildren().add(parametroNode);
        operadorContainer.getChildren().add(operadorNode);

        linha.getChildren().addAll(referenciaChoiceBox, operadorContainer, parametroContainer, periodicidade, remover);
        containerCondicoes.getChildren().add(linha);
    }

    private Node criarOperadorNode(Referencia referencia, Pane container) {
        switch (referencia) {
            case Referencia.IDADE:
                ChoiceBox<Operador> operadorChoiceBoxIdade = new ChoiceBox<>(); // faixa de idade
                operadorChoiceBoxIdade.getItems().addAll(Operador.values());
                return operadorChoiceBoxIdade;

            case Referencia.SETOR:
                ChoiceBox<Operador> operadorChoiceBoxSetor = new ChoiceBox<>();
                operadorChoiceBoxSetor.getItems().addAll(Operador.IGUAL,Operador.DIFERENTE) ;
                return operadorChoiceBoxSetor;

            case Referencia.ENFERMIDADE:
                ChoiceBox<Operador> operadorChoiceBoxEnfermidade = new ChoiceBox<>();
                operadorChoiceBoxEnfermidade.getItems().addAll(Operador.IGUAL);
                return operadorChoiceBoxEnfermidade;

            default:
                return new Label("Inválido");
        }
    }

    private Node criarParametroNode(Referencia referencia, Pane container) {
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

}
