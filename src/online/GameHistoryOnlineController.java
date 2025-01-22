/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class GameHistoryOnlineController implements Initializable {

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
                String playerXName = reader.readLine();
                String playerOName = reader.readLine();

                Platform.runLater(() -> {
                    playerLabel.setText(playerXName);
                    computerLabel.setText(playerOName);
                });

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.matches("^[XO] [1-9]$")) {
                        String finalLine = line;
                        Platform.runLater(() -> updateGameBoard(finalLine));
                        Thread.sleep(1000); 
                    }
                }

                
                Platform.runLater(() -> {
                    List<Integer> winningCells = checkWinner();
                    if (!winningCells.isEmpty()) {
                        highlightWinningCells(winningCells);
                    }
                });
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

        
        List<Integer> winningCells = checkWinner();
        if (!winningCells.isEmpty()) {
            highlightWinningCells(winningCells);
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

    private List<Integer> checkWinner() {
        List<Integer> winningCells = new ArrayList<>();

      
        for (int i = 0; i < 3; i++) {
            if (!getButtonByIndex(i * 3).getText().isEmpty() &&
                getButtonByIndex(i * 3).getText().equals(getButtonByIndex(i * 3 + 1).getText()) &&
                getButtonByIndex(i * 3 + 1).getText().equals(getButtonByIndex(i * 3 + 2).getText())) {
                winningCells.add(i * 3);
                winningCells.add(i * 3 + 1);
                winningCells.add(i * 3 + 2);
                return winningCells;
            }
        }

       
        for (int i = 0; i < 3; i++) {
            if (!getButtonByIndex(i).getText().isEmpty() &&
                getButtonByIndex(i).getText().equals(getButtonByIndex(i + 3).getText()) &&
                getButtonByIndex(i + 3).getText().equals(getButtonByIndex(i + 6).getText())) {
                winningCells.add(i);
                winningCells.add(i + 3);
                winningCells.add(i + 6);
                return winningCells;
            }
        }

     
        if (!getButtonByIndex(0).getText().isEmpty() &&
            getButtonByIndex(0).getText().equals(getButtonByIndex(4).getText()) &&
            getButtonByIndex(4).getText().equals(getButtonByIndex(8).getText())) {
            winningCells.add(0);
            winningCells.add(4);
            winningCells.add(8);
            return winningCells;
        }

       
        if (!getButtonByIndex(2).getText().isEmpty() &&
            getButtonByIndex(2).getText().equals(getButtonByIndex(4).getText()) &&
            getButtonByIndex(4).getText().equals(getButtonByIndex(6).getText())) {
            winningCells.add(2);
            winningCells.add(4);
            winningCells.add(6);
            return winningCells;
        }

        return winningCells;
    }

    private void highlightWinningCells(List<Integer> winningCells) {
        for (int index : winningCells) {
            Button button = getButtonByIndex(index);
            if (button != null) {
            
                String currentStyle = button.getStyle();
                
                
                button.setStyle(
                    currentStyle + 
                    "-fx-background-color: lightgreen; " 
                );
            }
        }
    }
}