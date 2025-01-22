/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import Player.PlayerSocket;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import org.json.simple.JSONObject;

public class GameController implements Initializable {

    private static GameController instance;
    @FXML
    private Button p1, p2, p3, p4, p5, p6, p7, p8, p9;
    @FXML
    private Label playerXLabel, turnLabel, playerOLabel;

    private Button[][] boardButtons;
    private String playerSymbol;
    private boolean isMyTurn;
    private PlayerSocket playerSocket;

    private String currentGameFileName;
    private static final String SAVE_FOLDER = "saved_games"; 

    private String playerXName;
    private String playerOName;
    private boolean namesSaved = false; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        playerSocket = PlayerSocket.getInstance();
        System.out.println("GameController initializing...");
        boardButtons = new Button[][]{
            {p1, p2, p3},
            {p4, p5, p6},
            {p7, p8, p9}
        };

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final int row = i;
                final int col = j;
                boardButtons[i][j].setOnAction((e) -> handleMove(row, col));
            }
        }

        ensureGameRecordsFolderExists();
    }

    private void ensureGameRecordsFolderExists() {
        File folder = new File(SAVE_FOLDER);
        if (!folder.exists()) {
            if (folder.mkdir()) {
                System.out.println("Folder created: " + SAVE_FOLDER);
            } else {
                System.out.println("Failed to create folder: " + SAVE_FOLDER);
            }
        } else {
            System.out.println("Folder already exists: " + SAVE_FOLDER);
        }
    }

    public void initializeGame(String symbol, String opponent) {
        this.playerSymbol = symbol;
        this.isMyTurn = symbol.equals("X"); 

        playerXName = symbol.equals("X") ? 
            PlayerSocket.getInstance().getLoggedInPlayer().getUsername() : opponent;
        playerOName = symbol.equals("O") ? 
            PlayerSocket.getInstance().getLoggedInPlayer().getUsername() : opponent;

        playerXLabel.setText(playerXName);
        playerOLabel.setText(playerOName);

        updateTurnLabel();

        String baseFileName = SAVE_FOLDER + "/" + playerXName + "_vs_" + playerOName + ".txt";
        currentGameFileName = getUniqueFileName(baseFileName);  
        System.out.println("Current game file: " + currentGameFileName);

        savePlayerNamesToFile();
    }

    private String getUniqueFileName(String baseFileName) {
        File file = new File(baseFileName);
        if (!file.exists()) {
            return baseFileName; 
        }
        int counter = 1;
        String newFileName;
        do {
            newFileName = baseFileName.replace(".txt", "_" + counter + ".txt");
            file = new File(newFileName);
            counter++;
        } while (file.exists());

        System.out.println("Unique file name generated: " + newFileName); 
        return newFileName;
    }

    private void savePlayerNamesToFile() {
        if (!namesSaved) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentGameFileName, true))) {
                writer.write("Player X: " + playerXName + "\n");
                writer.write("Player O: " + playerOName + "\n");
                writer.write("\n"); 
                namesSaved = true; 
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateTurnLabel() {
        turnLabel.setText(isMyTurn ? "Your turn" : "Opponent's turn");
    }

    private void handleMove(int row, int col) {
        if (!isMyTurn || !boardButtons[row][col].getText().isEmpty()) {
            System.out.println("Invalid move: Not your turn or cell is not empty.");
            return;
        }

        System.out.println("Player " + playerSymbol + " made a move at (" + row + ", " + col + ")");

        boardButtons[row][col].setText(playerSymbol);
        boardButtons[row][col].setStyle("-fx-text-fill: red; -fx-font-size: 45; -fx-font-weight: bold;");
        isMyTurn = false;
        updateTurnLabel();

        int cellNumber = (row * 3) + col + 1;
        saveMoveToFile(playerSymbol + " " + cellNumber);

        JSONObject moveData = new JSONObject();
        moveData.put("type", "gameMove");
        moveData.put("row", String.valueOf(row));
        moveData.put("col", String.valueOf(col));
        moveData.put("symbol", playerSymbol);

        playerSocket.sendJSON(moveData);

        List<int[]> winningCells = checkWinner();
        if (!winningCells.isEmpty()) {
            highlightWinningCells(winningCells);
            return; 
        }

     
        boolean isDraw = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (boardButtons[i][j].getText().isEmpty()) {
                    isDraw = false;
                    break;
                }
            }
        }
        if (isDraw) {
            
        }
    }

    public void updateBoard(int row, int col, String symbol) {
        boardButtons[row][col].setText(symbol);
        boardButtons[row][col].setStyle("-fx-text-fill: blue; -fx-font-size: 45; -fx-font-weight: bold;");
        isMyTurn = !symbol.equals(playerSymbol);
        updateTurnLabel();

        int cellNumber = (row * 3) + col + 1; 
        saveMoveToFile(symbol + " " + cellNumber);

        
        List<int[]> winningCells = checkWinner();
        if (!winningCells.isEmpty()) {
            highlightWinningCells(winningCells);
            return;
        }

    
        boolean isDraw = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (boardButtons[i][j].getText().isEmpty()) {
                    isDraw = false;
                    break;
                }
            }
        }
        if (isDraw) {
            
        }
    }

    private List<int[]> checkWinner() {
        List<int[]> winningCells = new ArrayList<>();

        
        for (int i = 0; i < 3; i++) {
            if (!boardButtons[i][0].getText().isEmpty() &&
                boardButtons[i][0].getText().equals(boardButtons[i][1].getText()) &&
                boardButtons[i][1].getText().equals(boardButtons[i][2].getText())) {
                winningCells.add(new int[]{i, 0});
                winningCells.add(new int[]{i, 1});
                winningCells.add(new int[]{i, 2});
                return winningCells;
            }
        }

        
        for (int i = 0; i < 3; i++) {
            if (!boardButtons[0][i].getText().isEmpty() &&
                boardButtons[0][i].getText().equals(boardButtons[1][i].getText()) &&
                boardButtons[1][i].getText().equals(boardButtons[2][i].getText())) {
                winningCells.add(new int[]{0, i});
                winningCells.add(new int[]{1, i});
                winningCells.add(new int[]{2, i});
                return winningCells;
            }
        }

       
        if (!boardButtons[0][0].getText().isEmpty() &&
            boardButtons[0][0].getText().equals(boardButtons[1][1].getText()) &&
            boardButtons[1][1].getText().equals(boardButtons[2][2].getText())) {
            winningCells.add(new int[]{0, 0});
            winningCells.add(new int[]{1, 1});
            winningCells.add(new int[]{2, 2});
            return winningCells;
        }

      
        if (!boardButtons[0][2].getText().isEmpty() &&
            boardButtons[0][2].getText().equals(boardButtons[1][1].getText()) &&
            boardButtons[1][1].getText().equals(boardButtons[2][0].getText())) {
            winningCells.add(new int[]{0, 2});
            winningCells.add(new int[]{1, 1});
            winningCells.add(new int[]{2, 0});
            return winningCells;
        }

        return winningCells; 
    }

    private void highlightWinningCells(List<int[]> winningCells) {
        for (int[] cell : winningCells) {
            int row = cell[0];
            int col = cell[1];
            Button button = boardButtons[row][col];
            if (button != null) {
                
                String currentStyle = button.getStyle();
                
                
                button.setStyle(
                    currentStyle +
                    "-fx-background-color: lightgreen; " 
                );
            }
        }
    }

    public void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardButtons[i][j].setText(""); 
                boardButtons[i][j].setStyle(""); 
            }
        }

        if (new File(currentGameFileName).exists()) {
            deleteTemporaryFile();
        }
    }

    private void saveMoveToFile(String move) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentGameFileName, true))) {
            if (new File(currentGameFileName).exists()) {
                writer.write(move + "\n");
                System.out.println("Move saved: " + move); 
            } else {
                System.out.println("File does not exist: " + currentGameFileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void askUserToSaveGame() {
        Platform.runLater(() -> {
            if (!new File(currentGameFileName).exists()) {
                System.out.println("No game to save.");
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Save Game");
            alert.setHeaderText("Do you want to save the game?");
            alert.setContentText("Choose your option.");

            ButtonType saveButton = new ButtonType("Save");
            ButtonType discardButton = new ButtonType("Discard");
            alert.getButtonTypes().setAll(saveButton, discardButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == saveButton) {
                moveFileToGameHistory();
            } else {
                deleteTemporaryFile();
            }
        });
    }

    public void moveFileToGameHistory() {
        File file = new File(currentGameFileName);
        if (file.exists()) {
            File destination = new File(SAVE_FOLDER + "/" + file.getName());
            if (file.renameTo(destination)) {
                System.out.println("File moved to saved_games folder.");
            } else {
                System.out.println("Failed to move the file. It may be due to permissions or the destination folder not existing.");
            }
        } else {
            System.out.println("Temporary file does not exist: " + currentGameFileName);
        }
    }

    public void deleteTemporaryFile() {
        File file = new File(currentGameFileName);
        if (file.exists()) {
            file.delete();
        }
    }
}