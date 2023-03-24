/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadstocks.stocks;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 *
 * @author samue
 */
public class ProgressDialog {

    Stage stage;

    public ProgressDialog() {
        init();
    }

    private void init() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/deadstocks/stocks/progressBar.fxml"));
            Scene scene = new Scene(root);
          
            stage = new Stage(StageStyle.UTILITY);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent e) {
                    e.consume();
                }
            });
            stage.setScene(scene);
        } catch (Exception ex) {
            Logger.getLogger(ProgressDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void show() {
        stage.show();
    }

    public void close() {
        stage.hide();
    }

    private class DialogController {

    }
}
