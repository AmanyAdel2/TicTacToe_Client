/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online;

import Player.PlayerSocket;
import Popups.PopUps;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import login.LoginController;
import org.json.simple.JSONObject;

public class OnlineController implements Initializable {

    @FXML
    private Button sendbtn;
    @FXML
    private TextField nametxt;
    @FXML
    private TextField scoretxt;
    @FXML
    private MenuButton historyMenu;
    @FXML
    private ListView<String> onlinePlayersList;
    @FXML
    private ListView<String> recordsListView;

    String selectedPlayer = "";
    PlayerSocket playerSocket;

    @FXML
    private Button logoutbtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        playerSocket = PlayerSocket.getInstance();
        System.out.println("OnlineController initializing...");

        
        nametxt.setText(playerSocket.getLoggedInPlayer().getUsername());
        nametxt.setEditable(false); 
        scoretxt.setText(Integer.toString(playerSocket.getPlayerScore()));
        scoretxt.setEditable(false); 
    

        onlinePlayersList.setItems(FXCollections.observableArrayList(playerSocket.getOnlinePlayers()));

       
        playerSocket.getOnlinePlayers().addListener((SetChangeListener.Change<? extends String> c) -> {
            if (c.wasAdded()) {
                if (!onlinePlayersList.getItems().contains(c.getElementAdded())) {
                    onlinePlayersList.getItems().add(c.getElementAdded());
                }
            }
            if (c.wasRemoved()) {
                onlinePlayersList.getItems().remove(c.getElementRemoved());
            }
            onlinePlayersList.refresh();
        });

    
        onlinePlayersList.setOnMouseClicked(event -> {
            String selected = onlinePlayersList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedPlayer = selected;
                System.out.println("Selected player: " + selectedPlayer);
            }
        });

        
        loadRecordFiles();
    }

    private void loadRecordFiles() {
        File folder = new File("saved_games");
        if (!folder.exists() || !folder.isDirectory()) {
            return;
        }

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            return;
        }

       
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

     
        for (File file : files) {
            String fileName = file.getName();
            String dateTime = extractDateAndTimeFromFile(file);
            if (dateTime != null) {
                String displayName = fileName + " - " + dateTime;
                recordsListView.getItems().add(displayName);
            }
        }

        
        recordsListView.setOnMouseClicked(event -> {
            String selectedFileName = recordsListView.getSelectionModel().getSelectedItem();
            if (selectedFileName != null) {
                String actualFileName = selectedFileName.split(" - ")[0];
                showFileOptions(actualFileName);
            }
        });
    }

    private void showFileOptions(String fileName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("File Options");
        alert.setHeaderText("Choose an action for the file: " + fileName);
        alert.setContentText("What would you like to do with this file?");
        alert.getDialogPane().setPrefSize(450, 200);
       
        ButtonType openButton = new ButtonType("Open");
        ButtonType deleteButton = new ButtonType("Delete");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonType.CANCEL.getButtonData());

        
        alert.getButtonTypes().setAll(openButton, deleteButton, cancelButton);

       
        alert.getDialogPane().setStyle(
            "-fx-background-color: beige;" +
            "-fx-font-size: 16;" +
            "-fx-font-weight: bold;"
        );

        
        alert.getDialogPane().lookupButton(openButton).setStyle(
            "-fx-background-color: lightgreen;" +
            "-fx-font-size: 14;" +
            "-fx-font-weight: bold;"
        );

        
        alert.getDialogPane().lookupButton(deleteButton).setStyle(
            "-fx-background-color: lightcoral;" +
            "-fx-font-size: 14;" +
            "-fx-font-weight: bold;"
        );

       
        alert.getDialogPane().lookupButton(cancelButton).setStyle(
            "-fx-background-color: lightgray;" +
            "-fx-font-size: 14;" +
            "-fx-font-weight: bold;"
        );

       
        alert.showAndWait().ifPresent(response -> {
            if (response == openButton) {
                openRecord(fileName);
            } else if (response == deleteButton) {
                deleteRecord(fileName);
            }
        });
    }

    private void deleteRecord(String fileName) {
        File fileToDelete = new File("saved_games/" + fileName);
        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                recordsListView.getItems().removeIf(item -> item.startsWith(fileName));
                System.out.println("File deleted successfully.");
                loadRecordFiles(); // إعادة تحميل الملفات بعد الحذف
            } else {
                System.out.println("Failed to delete the file.");
            }
        } else {
            System.out.println("File does not exist.");
        }
    }
    private void openRecord(String fileName) {
        try {
            Stage recordStage = new Stage();
            recordStage.setTitle("Game Record: " + fileName);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/online/GameHistoryOnline.fxml"));
            Parent root = loader.load();

            GameHistoryOnlineController historyController = loader.getController();
            historyController.loadGameMovesFromFile("saved_games/" + fileName);

            recordStage.setScene(new Scene(root));
            recordStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String extractDateAndTimeFromFile(File file) {
        long lastModified = file.lastModified();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        return sdf.format(new Date(lastModified));
    }

    @FXML
    private void onBack(ActionEvent event) {
        String username = playerSocket.getLoggedInPlayer().getUsername();

        Map<String, String> map = new HashMap<>();
        map.put("type", "logout");
        map.put("username", username);

        try {
            playerSocket.sendJSON(map);
            Platform.runLater(() -> {
                FXCollections.observableArrayList(playerSocket.getOnlinePlayers()).remove(username);
            });
            Stage stage = (Stage) logoutbtn.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/tictactoe/tictactoe.fxml"));
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "An error occurred during login.");
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @FXML
    private void sendRequest(ActionEvent event) {
        if (!selectedPlayer.equals("")) {
            Map<String, String> request = new HashMap<>();
            request.put("type", "sendGameReq");
            request.put("challenger", playerSocket.getLoggedInPlayer().getUsername());
            request.put("challenged", selectedPlayer);
            playerSocket.sendJSON(request);
            onlinePlayersList.getSelectionModel().clearSelection();

            selectedPlayer = "";
            onlinePlayersList.refresh();
        } else {
            PopUps.showErrorAlert(playerSocket.getStage(), "Error", "Select a player");
            showAlert(Alert.AlertType.ERROR, "Error", "Select a player");
        }
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
    public void setScore(String score){
    scoretxt.setText(score);}

    public void setNametxt(TextField nametxt) {
        this.nametxt = nametxt;
    }
}