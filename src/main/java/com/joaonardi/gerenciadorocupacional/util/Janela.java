package com.joaonardi.gerenciadorocupacional.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class Janela {
    public FXMLLoader loader;
    public Stage stage;
    public void abrirJanela(String diretorioView, String tituloJanela){
        try {
            stage = new Stage();
             loader= new FXMLLoader(getClass().getResource(diretorioView));
            Parent root = loader.load();

            stage.setTitle(tituloJanela);
            stage.setScene(new Scene(root));
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void fecharJanela(Node node) {
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
}
