package com.joaonardi.gerenciadorocupacional.controller;

import com.joaonardi.gerenciadorocupacional.util.Janela;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class DuvidasErrosController extends Janela {

    public Hyperlink githubLink;
    public Hyperlink emailLink;

    @FXML
    void initialize() {
        emailLink.setTextFill(Color.BLUE);
        githubLink.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI(githubLink.getText()));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao Abrir link");
            }
        });

        emailLink.setOnAction(event -> {
            emailLink.setText("Copiado para a área de transferência!");
            emailLink.setTextFill(Color.GREEN);
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(e -> {
                        emailLink.setText("joaovitornard@gmail.com");
                        emailLink.setTextFill(Color.BLUE);
                    }
            );
            pause.play();
        });
    }
}
