/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package level;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
public class LevelController implements Initializable {

    @FXML
    private Button easybtn;
    @FXML
    private Button medbtn;
    @FXML
    private Button hardBtn;
    @FXML
    private Button backBtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }  
    @FXML
    private void gogame(MouseEvent event) throws IOException {
        Stage stage = (Stage) easybtn.getScene().getWindow();
        medbtn.getScene().getWindow();
//        hardbtn.getScene().getWindow();
        
        
        Parent root = FXMLLoader.load(getClass().getResource("/game/Game.fxml"));
        stage.setScene(new Scene(root));
        
    }
    
    @FXML
    private void goHardGame(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hardCompMode/hardCompMode.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) hardBtn.getScene().getWindow();
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            //Set a title for the window
            stage.setTitle("Hard Mode");
            
            // Show the updated stage
            stage.show();
        } catch (IOException ex) {
            
            System.out.println("errrrorrr");
            Logger.getLogger(LevelController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @FXML
    private void goBackToMain(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tictactoe/tictactoe.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backBtn.getScene().getWindow();
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            //Set a title for the window
            stage.setTitle("Main Page");
            
            // Show the updated stage
            stage.show();
        } catch (IOException ex) {
            
            System.out.println("errrrorrr");
            Logger.getLogger(LevelController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
