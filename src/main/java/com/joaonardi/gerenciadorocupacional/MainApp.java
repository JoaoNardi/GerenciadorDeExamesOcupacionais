package com.joaonardi.gerenciadorocupacional;

import com.joaonardi.gerenciadorocupacional.util.DBBackup;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    public static Stage STAGE_PRINCIPAL;
    @Override
    public void start(Stage primaryStage) {
        try {
            // Carrega o arquivo FXML
            DBBackup.verificarOuRestaurarBanco(); // garante banco válido ou restaura/cria
            DBBackup.criarBackupDiario();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setTitle("Gerenciador Ocupacional");
            Image icon = new Image(
                    getClass().getResourceAsStream("/icons/app-icon.png")
            );

            primaryStage.getIcons().add(icon);            primaryStage.setScene(scene);
            STAGE_PRINCIPAL = primaryStage;
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar o arquivo FXML: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        launch(args);
    }
}