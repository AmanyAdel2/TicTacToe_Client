/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
package easyLevel;

import java.util.Random;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EasyLevelController {
    private char[][] board = new char[3][3];
    private boolean playerTurn = true; 

    @FXML
    private Button button0, button1, button2, button3, button4, button5, button6, button7, button8;

    @FXML
    private void playerMove(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();

        
        if (clickedButton.getText().isEmpty() && playerTurn) {
            clickedButton.setText("X");
            updateBoard(clickedButton, 'X');
            playerTurn = false;

            
            if (checkWinner('X')) {
                showEndGameAlert("Player Wins!");
                return;
            }

            
            if (isBoardFull()) {
                showEndGameAlert("It's a Draw!");
                return;
            }

            
            computerMove();
        }
    }

    private void computerMove() {
        Random random = new Random();

        while (!playerTurn) {
            int move = random.nextInt(9);
            Button button = getButtonByIndex(move);

            if (button.getText().isEmpty()) {
                button.setText("O");
                updateBoard(button, 'O');
                playerTurn = true;

                
                if (checkWinner('O')) {
                    showEndGameAlert("Computer Wins!");
                }

                
                if (isBoardFull()) {
                    showEndGameAlert("It's a Draw!");
                }
            }
        }
    }

    private Button getButtonByIndex(int index) {
        switch (index) {
            case 0: return button0;
            case 1: return button1;
            case 2: return button2;
            case 3: return button3;
            case 4: return button4;
            case 5: return button5;
            case 6: return button6;
            case 7: return button7;
            case 8: return button8;
            default: return null;
        }
    }

    private void updateBoard(Button button, char symbol) {
        int index = Integer.parseInt(button.getId().substring(6));
        board[index / 3][index % 3] = symbol;
    }

    private boolean checkWinner(char symbol) {
        
        for (int i = 0; i < 3; i++) {
            if ((board[i][0] == symbol && board[i][1] == symbol && board[i][2] == symbol) ||
                (board[0][i] == symbol && board[1][i] == symbol && board[2][i] == symbol)) {
                return true;
            }
        }
        return (board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol) ||
               (board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol);
    }

    private boolean isBoardFull() {
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == '\0') {
                    return false;
                }
            }
        }
        return true;
    }

    private void showEndGameAlert(String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(message);
        alert.setContentText("Choose an option:");

        ButtonType replayButton = new ButtonType("Replay");
        ButtonType backButton = new ButtonType("Back");

        alert.getButtonTypes().setAll(replayButton, backButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == replayButton) {
                resetGame();
            } else if (response == backButton) {
                backToPreviousScene();
            }
        });
    }

    private void resetGame() {
       
        button0.setText("");
        button1.setText("");
        button2.setText("");
        button3.setText("");
        button4.setText("");
        button5.setText("");
        button6.setText("");
        button7.setText("");
        button8.setText("");

        board = new char[3][3];
        playerTurn = true;
    }

    private void backToPreviousScene() {
        try {
           
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameLevel/GameLevel.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) button0.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Game Level");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
*/