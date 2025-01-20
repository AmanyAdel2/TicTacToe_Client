/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online;

import Player.PlayerSocket;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import register.RegiserController;

/**
 * FXML Controller class
 *
 * @author Al Badr
 */
public class OnlineController implements Initializable {

    @FXML
    private Button sendbtn;
    @FXML
    private TextField nametxt;


    @FXML
    private TextField scoretxt;
    @FXML
    private Button backbtn;
    @FXML
    private MenuButton historyMenu;
    @FXML
    private ListView<String> onlinePlayersList;
    
    String selectedPlayer = "";
    
    PlayerSocket playerSocket;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        playerSocket = PlayerSocket.getInstance();
        System.out.println("OnlineController");
        
        onlinePlayersList.getSelectionModel().selectedItemProperty().addListener((observable) -> {
            selectedPlayer = onlinePlayersList.getSelectionModel().getSelectedItem();
        });
        nametxt.setText(playerSocket.getLoggedInPlayer().getUsername());
        scoretxt.setText(Integer.toString(playerSocket.getLoggedInPlayer().getScore()));
        
        onlinePlayersList.setItems(FXCollections.observableArrayList(playerSocket.getOnlinePlayers()));
        
        playerSocket.getOnlinePlayers().addListener((SetChangeListener.Change<? extends String> c) -> {
            if (c.wasAdded()) {
                if(!onlinePlayersList.getItems().contains(c.getElementAdded())) {
                    onlinePlayersList.getItems().add(c.getElementAdded());
                }
            }
            if (c.wasRemoved()) {
                onlinePlayersList.getItems().remove(c.getElementRemoved());
            }
            onlinePlayersList.refresh();
        }); 
    }    


    @FXML
    private void onBack(ActionEvent event) {
//        try {
//            Stage stage = (Stage) backbtn.getScene().getWindow();
//            Parent root = FXMLLoader.load(getClass().getResource("/login/Login.fxml"));
//            stage.setScene(new Scene(root));
//        } catch (IOException ex) {
//            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to navigate back to the login screen.");
//            Logger.getLogger(OnlineController.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    @FXML
    private void sendRequest(ActionEvent event) {
        
        if(!selectedPlayer.equals("")){
            Map<String, String> request = new HashMap<>();
            request.put("type", "sendGameReq");
            request.put("challenger", playerSocket.getLoggedInPlayer().getUsername());
            request.put("challenged", selectedPlayer);
            playerSocket.sendJSON(request);
            onlinePlayersList.getSelectionModel().clearSelection();
        
            selectedPlayer = ""; // Reset selected player
            onlinePlayersList.refresh();
        }
        else showAlert(Alert.AlertType.ERROR, " Error", "Select a player");  
    }
    
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    
    public TextField getNametxt() {
        return nametxt;
    }

    public void setNametxt(TextField nametxt) {
        this.nametxt = nametxt;
    }
}
