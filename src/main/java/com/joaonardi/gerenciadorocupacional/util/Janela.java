package com.joaonardi.gerenciadorocupacional.util;

import com.joaonardi.gerenciadorocupacional.MainApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Janela {
    public FXMLLoader loader;
    public Stage stage;

    public void abrirJanela(String diretorioView, String tituloJanela, Stage stagePai, Runnable aoFechar) {
        try {
            stage = new Stage();
            loader = new FXMLLoader(getClass().getResource(diretorioView));
            stage.initOwner(stagePai);
            stage.initModality(Modality.WINDOW_MODAL);
            Parent root = loader.load();

            stage.setTitle(tituloJanela);
            stage.setScene(new Scene(root));

            if (aoFechar != null) {
                stage.setOnHiding(e -> aoFechar.run());
            }
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fecharJanela(Node node) {
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
}
