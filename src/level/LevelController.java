/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package level;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class LevelController implements Initializable {

    @FXML
    private Button easybtn;
    @FXML
    private Button medbtn;
    @FXML
    private Button hardBtn;
    @FXML
    private Button backButton;
    @FXML
    private ListView<String> recordsListView;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadRecordFiles();
    }

    private void loadRecordFiles() {
        File folder = new File("game_records");
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

        
        alert.getDialogPane().setPrefSize(450, 230);

        
        ButtonType openButton = new ButtonType("Open");
        ButtonType deleteButton = new ButtonType("Delete");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonType.CANCEL.getButtonData());

        
        alert.getButtonTypes().setAll(openButton, deleteButton, cancelButton);

        
        alert.getDialogPane().setStyle(
                "-fx-background-color: beige;"
                + "-fx-font-size: 16;"
                + "-fx-font-weight: bold;"
        );

        
        alert.getDialogPane().lookupButton(openButton).setStyle(
                "-fx-background-color: lightgreen;"
                + "-fx-font-size: 14;"
                + "-fx-font-weight: bold;"
        );

        
        alert.getDialogPane().lookupButton(deleteButton).setStyle(
                "-fx-background-color: lightcoral;"
                + "-fx-font-size: 14;"
                + "-fx-font-weight: bold;"
        );

        
        alert.getDialogPane().lookupButton(cancelButton).setStyle(
                "-fx-background-color: lightgray;"
                + "-fx-font-size: 14;"
                + "-fx-font-weight: bold;"
        );

        
        alert.showAndWait().ifPresent(response -> {
            if (response == openButton) {
                openRecord(fileName);
            } else if (response == deleteButton) {
                deleteRecord(fileName);
            }
        });
    }

    private void openRecord(String fileName) {
        try {
            
            Stage recordStage = new Stage();
            recordStage.setTitle("Game Record: " + fileName);

          
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/level/GameHistory.fxml"));
            Parent root = loader.load();

           
            GameHistoryController historyController = loader.getController();
            historyController.loadGameMovesFromFile("game_records/" + fileName);

          
            recordStage.setScene(new Scene(root));
            recordStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteRecord(String fileName) {
        File fileToDelete = new File("game_records/" + fileName);
        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
             
                recordsListView.getItems().removeIf(item -> item.startsWith(fileName));
                System.out.println("File deleted successfully.");
            } else {
                System.out.println("Failed to delete the file.");
            }
        } else {
            System.out.println("File does not exist.");
        }
    }

    private String extractDateAndTimeFromFile(File file) {
        long lastModified = file.lastModified();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        return sdf.format(new Date(lastModified));
    }

    @FXML
    private void easyGame(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/easyGame/EasyGame.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Easy Level");
        stage.show();
    }

    @FXML
    private void intermediateGame(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/intermediateGame/IntermediateGame.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Intermediate Level");
        stage.show();
    }

    @FXML
    private void goHardGame(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/hardCompMode/hardCompMode.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Hard Level");
        stage.show();
    }

    @FXML
    private void backButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tictactoe/tictactoe.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}