/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hardCompMode;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author DeSkToP
 */
public class HardCompModeController implements Initializable {

    @FXML
    private Button button00;
    @FXML
    private Button button01;
    @FXML
    private Button button10;
    @FXML
    private Button button20;
    @FXML
    private Button button02;
    @FXML
    private Button button11;
    @FXML
    private Button button12;
    @FXML
    private Button button22;
    @FXML
    private Button button21;
    @FXML
    private Button backBtn;
    
    
    private Button[][] boardButtons;
    private char[][] board = new char[3][3]; // 3x3 board
    
    private boolean isGameOver = false; 
    private char turn = 'X';  
    @FXML
    private Button resetBtn;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        boardButtons = new Button[][] {
            { button00, button01, button02 },
            { button10, button11, button12 },
            { button20, button21, button22 }
        };

        for(int i = 0; i < 3; i++) 
        {
            for (int j = 0; j < 3; j++) 
            {
                board[i][j] = ' ';
            }
        }
    } 
    
    @FXML
    private void goBack(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/level/Level.fxml"));
            Parent root = loader.load(); 
            Stage stage = (Stage) backBtn.getScene().getWindow();
            
            Scene scene = new Scene(root);
            stage.setScene(scene);

            //Set a title for the window
            stage.setTitle("Level Page");

            // Show the updated stage
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(HardCompModeController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    
    @FXML
    private void handleButtonClick(ActionEvent event) {
        if(isGameOver)
        {
            return; 
        }
        System.out.println("Button clicked!");
        // object returns the object that triggered the event. (object) have to cast it
        Button clickedButton = (Button) event.getSource();
        int row = GridPane.getRowIndex(clickedButton);
        int col = GridPane.getColumnIndex(clickedButton);
        
      
        // Check if cell is empty
        if(board[row][col] == ' ') 
        { 
            board[row][col] = turn;
            
            clickedButton.setText(String.valueOf(turn));
            clickedButton.setStyle("-fx-text-fill: red; -fx-font-size: 45px; -fx-background-color: beige;");                
           
            checkGameState();

            if (!isGameOver) 
            {
                switchPlayer();
                // Let the computer play
                computerMove(); 
            }
        }
    }
    
    private void switchPlayer() {
        turn = (turn == 'X') ? 'O' : 'X';
    }
    
    
    private void checkGameState() {
        // Check rows, columns, and diagonals for a win
        if(checkWin('X'))
        {
            isGameOver = true;
            showAlert("You win!");
        }
        else if(checkWin('O'))
        {
            isGameOver = true;
            showAlert("Computer  wins!");
        }
        else if(isBoardFull()) 
        {
            isGameOver = true;
            showAlert("It's a draw!");
        }
    }
    
    private boolean checkWin(char player) {
        // Check rows and columns
        for(int i = 0; i < 3; i++) 
        {
            if((board[i][0] == player && board[i][1] == player && board[i][2] == player) || 
                (board[0][i] == player && board[1][i] == player && board[2][i] == player)) 
            {
                return true;
            }
        }
        // Check diagonals
        if((board[0][0] == player && board[1][1] == player && board[2][2] == player) ||
            (board[0][2] == player && board[1][1] == player && board[2][0] == player)) {
            return true;
        }
        
        return false;
    }
    
    
    private boolean isBoardFull() {
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                if (board[i][j] == ' ') return false;
            }
        }
        return true;
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    
    private void computerMove() {
        // Check if game is already over
        if(isGameOver) return;
        
        // Find best move using minimax
        int bestScore = Integer.MIN_VALUE;
        int bestRow = -1;
        int bestCol = -1;
        
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(board[i][j] == ' ') {
                    // Every possible move is made by the computer
                    board[i][j] = 'O';
                    int score = minimax(board, 0, false);
                    board[i][j] = ' ';
                    
                    if(score > bestScore) {
                        bestScore = score;
                        // once finding the best score, the computer will store the row and column of the best score
                        bestRow = i;
                        bestCol = j;
                    }
                }
            }
        }
        
        // Make the best move
        board[bestRow][bestCol] = 'O';
        Button button = getButtonByPosition(bestRow, bestCol);
        button.setText("O");
        button.setStyle("-fx-text-fill: blue; -fx-font-size: 45px; -fx-background-color: beige;");
        checkGameState();
        if(!isGameOver) switchPlayer();

        if(isGameOver) return;

//         // pick the first empty spot
//         for(int i = 0; i < 3; i++) 
//         {
//             for(int j = 0; j < 3; j++) 
//             {
//                 if (board[i][j] == ' ') 
//                 {
// //                  board[i][j] = turn;
//                     int score = minimax(board, 0, true);
//                     Button button = getButtonByPosition(i, j);
//                     button.setText(String.valueOf(turn));
//                     button.setStyle("-fx-font-size: 30px;");
//                     checkGameState();
//                     if (!isGameOver) switchPlayer();
//                     return;
//                 }
//             }
//         }
    }
      

    private int minimax(char[][] board, int depth, boolean isMaximizingPlayer){
        int score = evaluate(board);
        // if the computer wins, return the score
        if(score == 10) 
        {
            return score - depth;
        }
        
        // if the player wins, return the score 
        if(score == -10) 
        {
            // Adding depth to makes losses that occur at deeper levels of 
            // the recursion tree (i.e., later in the game) appear less bad
            return score + depth;
        }

        // if the game is a draw, return 0
        if(score == 0 && isBoardFull()) {
            return 0;
        }   

        // if the computer is maximizing, return the best score
        if(isMaximizingPlayer) {
            int best = -1000;
            
            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 3; j++) {
                    if(board[i][j] == ' ') {
                        board[i][j] = 'O';
                        best = Math.max(best, minimax(board, depth + 1, false));
                        board[i][j] = ' ';
                    }
                }
            }
            return best;
        } else {
            // if the computer is minimizing (player's turn), return the best score
            int best = 1000;
            
            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 3; j++) {
                    if(board[i][j] == ' ') {
                        board[i][j] = 'X'; 
                        best = Math.min(best, minimax(board, depth + 1, true));
                        board[i][j] = ' ';
                    }
                }
            }
            return best;
        }
        
    }
    
    private int evaluate(char[][] board) {
         // Check rows for victory
        for(int row = 0; row < 3; row++) 
        {
            if(board[row][0] == board[row][1] && board[row][1] == board[row][2]) 
            {
                if (board[row][0] == 'O') {
                    return +10;
                } else if (board[row][0] == 'X') {
                    return -10;
                }
            }
        }
        
         // Check columns for victory
        for (int col = 0; col < 3; col++) {
            if (board[0][col] == board[1][col] && board[1][col] == board[2][col]) {
                if (board[0][col] == 'O') {
                    return +10;
                } else if (board[0][col] == 'X') {
                    return -10;
                }
            }
        }
        
         // Check diagonals for victory
        if(board[0][0] == board[1][1] && board[1][1] == board[2][2]) 
        {
            if (board[0][0] == 'O') 
            {
                return +10;
            } else if (board[0][0] == 'X') 
            {
                return -10;
            }
        }

        if(board[0][2] == board[1][1] && board[1][1] == board[2][0]) 
        {
            if (board[0][2] == 'O') 
            {
                return +10;
            } else if (board[0][2] == 'X') 
            {
                return -10;
            }
        }

        return 0; // No winner yet
    }
    private Button getButtonByPosition(int row, int col) {
        if(row == 0 && col == 0){
            return button00;
        } 
        else if(row == 0 && col == 1){
            return button01;
        } 
        else if(row == 0 && col == 2){
            return button02;
        } 
        else if(row == 1 && col == 0){
            return button10;
        } 
        else if(row == 1 && col == 1){
            return button11;
        } 
        else if(row == 1 && col == 2){
            return button12;
        }
        else if (row == 2 && col == 0){
            return button20;
        } 
        else if (row == 2 && col == 1){
            return button21;
        } 
        else return button22;
    }
    @FXML
    private void resetGame(ActionEvent event) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                boardButtons[row][col].setText("");
                boardButtons[row][col].setStyle("-fx-background-color: beige;");
                board[row][col] = ' ';
                turn = 'X';  
                isGameOver = false;
            }
        }
    }
}
