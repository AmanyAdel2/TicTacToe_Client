/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import Player.PlayerSocket;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author HP
 */
public class LoginController implements Initializable {

    @FXML
    private TextField passtxt;
    @FXML
    private TextField usertxt;
    @FXML
    private Button signbtn;
    @FXML
    private Button regbtn;
    
    PlayerSocket playerSocket;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        playerSocket = PlayerSocket.getInstance();
    }    

    @FXML
    private void goReg(MouseEvent event) throws IOException {
     
        try {
            Stage stage = (Stage) regbtn.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/register/Register.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException ex) {
            System.out.println("errrrorrr");
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
     
    }
    
}
