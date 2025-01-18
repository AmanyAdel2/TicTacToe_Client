/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package level;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

public class GameHistoryController implements Initializable {

    @FXML
    private Button p1, p2, p3, p4, p5, p6, p7, p8, p9;
    @FXML
    private Text playerLabel;
    @FXML
    private Text computerLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
     
    }


    public void loadGameMovesFromFile(String fileName) {
        File currentFile = new File(fileName);
        if (currentFile.exists()) {
            loadGameMoves(currentFile); 
        } else {
            System.out.println("No current file found!");
        }
    }

    private void loadGameMoves(File file) {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.matches("^[XO] [1-9]$")) {
                        String finalLine = line;
                        Platform.runLater(() -> updateGameBoard(finalLine));
                        Thread.sleep(1000); 
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateGameBoard(String move) {
        String[] parts = move.split(" ");
        String player = parts[0];
        int position = Integer.parseInt(parts[1]) - 1; 

        Button buttonToUpdate = getButtonByIndex(position);
        if (buttonToUpdate != null) {
            buttonToUpdate.setText(player);
            buttonToUpdate.setStyle("-fx-text-fill: " + (player.equals("X") ? "red" : "blue") + "; -fx-font-size: 45; -fx-font-weight: bold;");
        }
    }

    private Button getButtonByIndex(int index) {
        switch (index) {
            case 0: return p1;
            case 1: return p2;
            case 2: return p3;
            case 3: return p4;
            case 4: return p5;
            case 5: return p6;
            case 6: return p7;
            case 7: return p8;
            case 8: return p9;
            default: return null;
        }
    }

    @FXML
    private void backButton(ActionEvent event) throws IOException {
        
    }
     @FXML
    private void openRecord(ActionEvent event) {
        
    }
}