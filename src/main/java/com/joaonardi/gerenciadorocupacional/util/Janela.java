package com.joaonardi.gerenciadorocupacional.util;

import com.joaonardi.gerenciadorocupacional.MainApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;

public class Janela<T> {
    public FXMLLoader loader;
    public Stage stage;

    public T objetoPrincipal;

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
    public void abrirJanelaGerenciar(String contexto, Runnable aoFechar) {
        try {
            stage = new Stage();
            loader = new FXMLLoader(getClass().getResource("/view/GerenciarBaseView.fxml"));
            stage.initOwner(MainApp.STAGE_PRINCIPAL);
            stage.initModality(Modality.WINDOW_MODAL);
            Parent root = loader.load();

            stage.setTitle("Gerenciar  " + contexto);
            stage.setScene(new Scene(root));

            if (aoFechar != null) {
                stage.setOnHiding(e -> aoFechar.run());
            }
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void set(T objeto){
        this.objetoPrincipal = objeto;
    }

    //todo implementar em todo o projeto
    public void salvar(String contexto,String acao,Node node, ActionSalvar actionSalvar ) {
        try {
            actionSalvar.executar();
            JOptionPane.showMessageDialog(null, "Registro: *" + contexto + "* " + acao + " com sucesso");
            fecharJanela(node);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao " + acao + " *" + contexto +"*: " + e.getMessage());
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
