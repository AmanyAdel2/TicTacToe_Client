/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import Player.PlayerSocket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author HP
 */
public class GameController implements Initializable {

    private static GameController instance;
    @FXML
    private Button p1;
    @FXML
    private Button p2;
    @FXML
    private Button p4;
    @FXML
    private Button p7;
    @FXML
    private Button p3;
    @FXML
    private Button p5;
    @FXML
    private Button p6;
    @FXML
    private Button p9;
    @FXML
    private Button p8;
    @FXML
    private Label playerXLabel;
    @FXML
    private Label turnLabel;
    @FXML
    private Label playerOLabel;

    private Button[][] boardButtons;
    private String playerSymbol; // "X" or "O"
    private boolean isMyTurn;
    private PlayerSocket playerSocket;
    
    

    /**
     * Initializes the controller class.
     */
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
    }  
    

    public void initializeGame(String symbol, String opponent) {
        //System.out.println("GameController initializing...");   
        this.playerSymbol = symbol;
        this.isMyTurn = symbol.equals("X"); // X goes first
        String myUsername = playerSocket.getLoggedInPlayer().getUsername();
        
        playerXLabel.setText(symbol.equals("X") ? 
            PlayerSocket.getInstance().getLoggedInPlayer().getUsername() : opponent);
        playerOLabel.setText(symbol.equals("O") ? 
            PlayerSocket.getInstance().getLoggedInPlayer().getUsername() : opponent);
        
        updateTurnLabel();
    }
    
    private void updateTurnLabel() {
        turnLabel.setText(isMyTurn ? "Your turn" : "Opponent's turn");
    }
    private void handleMove(int row, int col) {
        if (!isMyTurn || !boardButtons[row][col].getText().isEmpty()) {
            return;
        }
        
        // Send move to server
        JSONObject moveData = new JSONObject();
        moveData.put("type", "gameMove");
        moveData.put("row", String.valueOf(row));
        moveData.put("col", String.valueOf(col));
        moveData.put("symbol", playerSymbol);
        
        playerSocket.sendJSON(moveData);
        
        // Update the button
        boardButtons[row][col].setText(playerSymbol);
        isMyTurn = false;
        updateTurnLabel();
    }
    
    public void updateBoard(int row, int col, String symbol) {
        boardButtons[row][col].setText(symbol);
        isMyTurn = !symbol.equals(playerSymbol);
        updateTurnLabel();
    }
    
    public void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardButtons[i][j].setText("");
            }
        }
    }
    
     // Make sure to clear the instance when the game ends
    public static void clearInstance() {
        instance = null;
    }
}
