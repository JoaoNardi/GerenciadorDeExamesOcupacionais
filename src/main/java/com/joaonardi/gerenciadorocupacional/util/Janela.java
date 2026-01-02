package com.joaonardi.gerenciadorocupacional.util;

import com.joaonardi.gerenciadorocupacional.controller.JanelaGerenciar;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;

public class Janela<T> implements Editavel<T> {
    public Stage stage;

    public T objetoPrincipal;

    public <T, C extends Editavel<T>> C abrirJanela(
            String diretorioView,
            String tituloJanela,
            Stage stagePai,
            Runnable aoFechar,
            T objeto
    ) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(diretorioView));
            Parent root = loader.load();

            C controller = null;
            if (objeto != null) {
                controller = loader.getController();
                controller.set(objeto);
            }


            Stage stage = new Stage();
            stage.initOwner(stagePai);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle(tituloJanela);
            stage.setScene(new Scene(root));

            if (aoFechar != null) {
                stage.setOnHiding(e -> aoFechar.run());
            }

            stage.show();
            return controller;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public <T> JanelaGerenciar<T> abrirJanelaGerenciar(
            String titulo,
            Stage stagePai,
            Runnable aoFechar
    ) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/view/GerenciarBaseView.fxml"));
            Parent root = loader.load();

            JanelaGerenciar<T> controller = loader.getController();

            Stage stage = new Stage();
            stage.initOwner(stagePai);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle("Gerenciar " + titulo);
            stage.setScene(new Scene(root));

            if (aoFechar != null) {
                stage.setOnHiding(e -> aoFechar.run());
            }

            stage.show();
            return controller;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(T objeto) {
        this.objetoPrincipal = objeto;
    }

    //todo implementar em todo o projeto
    public void salvar(String contexto, String acao, Node node, ActionSalvar actionSalvar) {
        try {
            actionSalvar.executar();
            JOptionPane.showMessageDialog(null, "Registro: *" + contexto + "* " + acao + " com sucesso");
            fecharJanela(node);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao " + acao + " *" + contexto + "*: " + e.getMessage());
        }
    }

    public void fecharJanela(Node node) {
        try {
            Stage stage = (Stage) node.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao Fechar Janela");
        }
    }
}
