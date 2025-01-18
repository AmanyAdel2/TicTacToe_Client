/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyGame;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javafx.event.ActionEvent;

public class GameRecordController {

    @FXML
    private Button p1, p2, p3, p4, p5, p6, p7, p8, p9;

    public void initialize() {
        loadGameMoves(); 
    }
    

    private void loadGameMoves() {
        new Thread(() -> {
            File latestFile = getLatestGameRecord(); 
            if (latestFile == null) {
                System.out.println("No game records found!");
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(latestFile))) {
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

    private File getLatestGameRecord() {
        File folder = new File("game_records");
        if (!folder.exists() || !folder.isDirectory()) {
            return null; 
        }

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File latestFile = files[0];
        for (File file : files) {
            if (file.lastModified() > latestFile.lastModified()) {
                latestFile = file;
            }
        }
        return latestFile;
    }

    private void updateGameBoard(String move) {
        String[] parts = move.split(" ");
        
        String player = parts[0]; 
        int position = Integer.parseInt(parts[1]) - 1;

        Button buttonToUpdate = null;

        
        switch (position) {
            case 0: buttonToUpdate = p1; break;
            case 1: buttonToUpdate = p2; break;
            case 2: buttonToUpdate = p3; break;
            case 3: buttonToUpdate = p4; break;
            case 4: buttonToUpdate = p5; break;
            case 5: buttonToUpdate = p6; break;
            case 6: buttonToUpdate = p7; break;
            case 7: buttonToUpdate = p8; break;
            case 8: buttonToUpdate = p9; break;
        }

        
        if (buttonToUpdate != null) {
            buttonToUpdate.setText(player);
            buttonToUpdate.setStyle("-fx-text-fill: " + (player.equals("X") ? "red" : "blue") + "; -fx-font-size: 45; -fx-font-weight: bold;");
        }
    }

    @FXML
    private void openRecord(ActionEvent event) {
       
    }

    @FXML
    private void backButton(ActionEvent event) throws IOException {
        
    }
}