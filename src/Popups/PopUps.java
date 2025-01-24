/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Popups;

import Player.PlayerSocket;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Mohamed Sameh
 */
public class PopUps {

    public String showCustomDialog(Stage primaryStage, String challenger) {
        
        String result = "";
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Popups/CustomDialog.fxml"));
            Pane dialogPane = loader.load();

            // Get the controller
            CustomDialogController controller = loader.getController();
            controller.setTitle("Game Challenge");
            controller.setMessage(challenger + " wants to play against you?");

            // Create a new stage for the dialog
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage); // Set the owner stage
            dialogStage.setScene(new Scene(dialogPane));

            // Set actions for OK and Cancel buttons
            controller.setOnOkAction(() -> {
                System.out.println("OK clicked!");
                dialogStage.close(); // Close the dialog
            });

            controller.setOnCancelAction(() -> {
                System.out.println("Cancel clicked!");
                dialogStage.close(); // Close the dialog
            });

            // Show the dialog
            dialogStage.showAndWait();
            
            result = controller.getResult();
            
            
        } catch (IOException ex) {
            Logger.getLogger(PopUps.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
    
    public static void showErrorAlert(Stage owner, String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        // Set Title and Header
        alert.setTitle("");
        alert.setHeaderText(title);

        // Set Content
        VBox contentBox = new VBox(10);
        Label contentLabel = new Label(content);
        contentLabel.setFont(Font.font("System italic", FontWeight.BOLD, 14));
        contentLabel.setStyle("-fx-text-fill: #333333;");
        contentBox.getChildren().add(contentLabel);
        alert.getDialogPane().setContent(contentBox);

        // Style Dialog Pane
        alert.getDialogPane().setStyle(
            "-fx-background-color: linear-gradient(to bottom, #FFEBEB, #FFD6D6);" +
            "-fx-border-color: #FF6B6B;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 10px;" +
            "-fx-background-radius: 10px;" +
            "-fx-padding: 10px;"
        );

        // Style Header
        alert.getDialogPane().lookup(".header-panel").setStyle(
            "-fx-background-color: #FF6B6B;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 16px;" +
            "-fx-background-radius: 10px;" +
            "-fx-padding: 10px;"
        );

        // Safely Style OK Button
        Node okButton = alert.getDialogPane().lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.setStyle(
                "-fx-background-color: #FF6B6B;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 5px;" +
                "-fx-font-weight: bold;"
            );
        }

        // Set Owner
        if (owner != null) {
            alert.initOwner(owner);
        }

        // Show Alert
        alert.showAndWait();
    }

}
