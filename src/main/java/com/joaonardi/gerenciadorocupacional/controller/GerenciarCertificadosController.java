package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.model.Periodicidade;
import com.joaonardi.gerenciadorocupacional.model.TipoCertificado;
import com.joaonardi.gerenciadorocupacional.service.TipoCertificadoService;
import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class GerenciarCertificadosController {
    public TableView<TipoCertificado> tabelaCertificados;
    public TableColumn<TipoCertificado, String> colunaNome;
    public TableColumn<TipoCertificado, Integer> colunaPerioicidade;

    ObservableList<TipoCertificado> tiposCertificado;
    final Janela janela = new Janela();
    TipoCertificadoController tipoCertificadoController = new TipoCertificadoController();
    final TipoCertificadoService tipoCertificadoService = new TipoCertificadoService();

    @FXML
    public void initialize() {

        tiposCertificado = tipoCertificadoService.listarTiposCertificados();

        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaPerioicidade.setCellValueFactory(new PropertyValueFactory<>("periodicidade"));
        colunaPerioicidade.setCellFactory(c -> new TableCell<>(){
                    @Override
                    protected void updateItem(Integer periodicidade, boolean empty) {
                        super.updateItem(periodicidade, empty);
                        if (empty || periodicidade == null){
                            setText(" ");
                        }else {
                            Periodicidade periodo = Periodicidade.fromValor(periodicidade);
                            setText(String.valueOf(periodo));

                        }
                    }
                }
        );
        tabelaCertificados.setItems(tiposCertificado);
    }

    public void handleTableDoubleClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2){
            TipoCertificado tipoCertificadoSelecionado = tabelaCertificados.getSelectionModel().getSelectedItem();

            janela.abrirJanela("/view/TipoCertificadoView.fxml","Editar Certificado",null);
            tipoCertificadoController = janela.loader.getController();
            tipoCertificadoController.setTipoCertificado(tipoCertificadoSelecionado);
            janela.stage.setOnHidden(e -> {tiposCertificado.clear(); tiposCertificado.addAll(tipoCertificadoService.listarTiposCertificados());
                tabelaCertificados.setItems(tiposCertificado);
            });
        }
    }
}
