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
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
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

}
