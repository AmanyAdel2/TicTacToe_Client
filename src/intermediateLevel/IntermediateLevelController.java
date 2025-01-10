/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
package intermediateLevel;

import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.util.Random;
import javafx.scene.control.ButtonType;

public class InterMediateLevelController {

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
                showAlert("Congratulations!", "Player Wins!", true);
                return;
            }

            if (isBoardFull()) {
                showAlert("It's a Draw!", "No one wins!", true);
                return;
            }

            computerMove();
        }
    }

    private void computerMove() {
        int[] bestMove = findBestMove();

        if (bestMove != null) {
            Button button = getButtonByIndex(bestMove[0] * 3 + bestMove[1]);
            button.setText("O");
            updateBoard(button, 'O');
            playerTurn = true;

            if (checkWinner('O')) {
                showAlert("Game Over", "Computer Wins!", true);
            }
        }
    }

    private int[] findBestMove() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '\0') {
                    board[i][j] = 'O';
                    if (checkWinner('O')) {
                        board[i][j] = '\0';
                        return new int[]{i, j};
                    }
                    board[i][j] = '\0';
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '\0') {
                    board[i][j] = 'X';
                    if (checkWinner('X')) {
                        board[i][j] = '\0';
                        return new int[]{i, j};
                    }
                    board[i][j] = '\0';
                }
            }
        }

        if (board[1][1] == '\0') {
            return new int[]{1, 1};
        }

        int[][] corners = {{0, 0}, {0, 2}, {2, 0}, {2, 2}};
        for (int[] corner : corners) {
            if (board[corner[0]][corner[1]] == '\0') {
                return corner;
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '\0') {
                    return new int[]{i, j};
                }
            }
        }

        return null;
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
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '\0') {
                    return false;
                }
            }
        }
        return true;
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

    private void showAlert(String title, String message, boolean restart) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);

       
        if (message.equals("Computer Wins!")) {
            alert.setContentText("You Lost! The computer beat you.\n\nDo you want to restart or go back?");
        } else if (message.equals("Player Wins!")) {
            alert.setContentText("Congratulations! You Won!\n\nDo you want to restart or go back?");
        } else if (message.equals("No one wins!")) {
            alert.setContentText("It's a Draw! No one won.\n\nDo you want to restart or go back?");
        }

        ButtonType restartButton = new ButtonType("Restart");
        ButtonType backButton = new ButtonType("Back");

        alert.getButtonTypes().setAll(restartButton, backButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == restartButton) {
                resetGame();
            } else if (result.get() == backButton) {
                goBack();
            }
        }
    }
     private void goBack() {
        try {
            
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/gameLevel/GameLevel.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) button0.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Game Level");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '\0';
            }
        }
        button0.setText("");
        button1.setText("");
        button2.setText("");
        button3.setText("");
        button4.setText("");
        button5.setText("");
        button6.setText("");
        button7.setText("");
        button8.setText("");
        playerTurn = true;
    }
}

*/
