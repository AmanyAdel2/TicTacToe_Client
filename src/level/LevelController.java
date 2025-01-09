/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package level;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
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
    private Button hardbtn;

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
        hardbtn.getScene().getWindow();
        
        
        Parent root = FXMLLoader.load(getClass().getResource("/game/Game.fxml"));
        stage.setScene(new Scene(root));
        
    }
    
}
