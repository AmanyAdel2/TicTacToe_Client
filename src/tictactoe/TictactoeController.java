/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;

import Player.PlayerSocket;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author HP
 */
public class TictactoeController implements Initializable {

    @FXML
    private Button locbtn;
    @FXML
    private Button combtn;
    @FXML
    private Button onbtn; 

   /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        PlayerSocket socPlayer = PlayerSocket.getInstance();
        
        Platform.runLater(() -> {
            Stage primaryStage = (Stage) onbtn.getScene().getWindow();
            primaryStage.setOnCloseRequest(event -> {
                // Handle window close
            if (socPlayer.socket != null && !socPlayer.socket.isClosed()) {
                try {
                    socPlayer.socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(TictactoeController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            });
        });
    }    

    @FXML
    void goLevel(MouseEvent event) {
        try {
            Stage stage = (Stage) combtn.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/level/Level.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException ex) {
            
            System.out.println("errrrorrr");
            Logger.getLogger(TictactoeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @FXML
    private void gonline(MouseEvent event) {
        try {
            Stage stage = (Stage) onbtn.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/login/Login.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException ex) {
            System.out.println("errrrorrr");
            Logger.getLogger(TictactoeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @FXML
    private void golocalgame(MouseEvent event) throws IOException {
        Stage stage = (Stage) locbtn.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/playersName/PlayersName.fxml"));
        stage.setScene(new Scene(root));
    }
    
    
    
}
