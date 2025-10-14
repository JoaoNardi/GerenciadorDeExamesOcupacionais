package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Periodicidade;
import com.joaonardi.gerenciadorocupacional.model.TipoExame;
import com.joaonardi.gerenciadorocupacional.service.TipoExameService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class GerenciarExamesController {
    public TableView<TipoExame> tabelaExames;
    public TableColumn<TipoExame, String> colunaNome;
    public TableColumn<TipoExame, Integer> colunaPerioicidade;

    ObservableList<TipoExame> tiposExame;
    final Janela janela = new Janela();
    TipoExameController tipoExameController = new TipoExameController();
    final TipoExameService tipoExameService = new TipoExameService();

    @FXML
    public void initialize() {

        tiposExame = tipoExameService.listarTiposExame();

        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaPerioicidade.setCellValueFactory(new PropertyValueFactory<>("periodicidade"));
        colunaPerioicidade.setCellFactory(c -> new TableCell<>(){
                    @Override
                    protected void updateItem(Integer periodicidade, boolean empty) {
                        super.updateItem(periodicidade, empty);
                        if (empty || periodicidade == null){
                            setText(null);
                        }else {
                            Periodicidade periodo = Periodicidade.fromValor(periodicidade);
                            setText(periodo.toString());

                        }
                    }
                }
        );
        tabelaExames.setItems(tiposExame);
    }

    public void handleTableDoubleClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2){
            TipoExame tipoExameSelecionado = tabelaExames.getSelectionModel().getSelectedItem();

            janela.abrirJanela("/view/TipoExameView.fxml","Editar Exame", null);
            tipoExameController = janela.loader.getController();
            tipoExameController.setTipoExame(tipoExameSelecionado);
            janela.stage.setOnHidden(e -> {tiposExame.clear(); tiposExame.addAll(tipoExameService.listarTiposExame());
                tabelaExames.setItems(tiposExame);
            });
        }
    }
}
