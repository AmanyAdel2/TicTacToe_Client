/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyGame;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class EasyGameController implements Initializable {

    @FXML
    private Text playerLabel;

    @FXML
    private Text computerLabel;

    @FXML
    private Button p1, p2, p3, p4, p5, p6, p7, p8, p9;

    private EasyGameLogic logic = new EasyGameLogic();
    private int playerScore = 0;
    private int computerScore = 0;
    private String player = "Player";
    private String computer = "Computer";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resetGame();
    }

    private void handleButtonPress(Button button) {
        int index = Integer.parseInt(button.getId().substring(1)) - 1; 
        int row = index / 3;
        int col = index % 3;

        
        if (logic.makeMove(row, col, 'X')) {
            button.setText('X' + "");
            button.setStyle("-fx-text-fill: red; -fx-font-size: 45; -fx-font-weight: bold;");

            if (logic.checkWinner('X')) {
                showGameOverAlert(player + " Wins!");
                playerScore++;
                updateScores();
                return;
            }
            if (logic.isBoardFull()) {
                showGameOverAlert("It's a Draw!");
                return;
            }
            computerMove();
        }
    }

    private void computerMove() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (logic.getBoard()[i][j] == '-') {
                    logic.makeMove(i, j, 'O');
                    Button button = getButtonByRowCol(i, j);
                    if (button != null) {
                        button.setText('O' + "");
                        button.setStyle("-fx-text-fill: blue; -fx-font-size: 45; -fx-font-weight: bold;");
                    }
                    if (logic.checkWinner('O')) {
                        showGameOverAlert(computer + " Wins!");
                        computerScore++;
                        updateScores();
                    }
                    if (logic.isBoardFull()) {
                        showGameOverAlert("It's a Draw!");
                    }
                    return;
                }
            }
        }
    }

    private void showGameOverAlert(String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(message);
        alert.setContentText("Choose your next action:");

        ButtonType playAgainButton = new ButtonType("Play Again");
        ButtonType backButton = new ButtonType("Back");
        alert.getButtonTypes().setAll(playAgainButton, backButton);
        alert.setGraphic(null);
        alert.getDialogPane().setStyle(
            "-fx-background-color: beige;" +
            "-fx-font-size: 16;" +
            "-fx-font-weight: bold;"
        );
        alert.getDialogPane().lookupButton(playAgainButton).setStyle(
            "-fx-background-color: lightgreen;" +
            "-fx-font-size: 14;" +
            "-fx-font-weight: bold;"
        );
        alert.getDialogPane().lookupButton(backButton).setStyle(
            "-fx-background-color: lightcoral;" +
            "-fx-font-size: 14;" +
            "-fx-font-weight: bold;"
        );
        alert.showAndWait().ifPresent(response -> {
            if (response == playAgainButton) {
                resetGame();
            } else {
                playerScore = 0;
                computerScore = 0;
                updateScores();
                goToBackScene();
            }
        });
    }

    private void resetGame() {
        logic.resetGame();
        for (Button button : new Button[]{p1, p2, p3, p4, p5, p6, p7, p8, p9}) {
            button.setText("");
            button.setStyle("-fx-background-color: beige; -fx-font-size: 14; -fx-font-weight: bold;");
            button.setOnAction(e -> handleButtonPress(button));
        }
    }

    private void goToBackScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/level/Level.fxml"));
            Stage stage = (Stage) p1.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateScores() {
        playerLabel.setText(player + " (" + playerScore + ")");
        computerLabel.setText(computer + " (" + computerScore + ")");
    }

    @FXML
    private void backButton(ActionEvent event) throws IOException {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Back Confirmation");
        alert.setHeaderText("Are you sure you want to go back?");
        alert.setContentText("Any unsaved progress will be lost.");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            goToBackScene();
        }
    }

    private Button getButtonByRowCol(int row, int col) {
        switch (row) {
            case 0:
                return (col == 0) ? p1 : (col == 1) ? p2 : p3;
            case 1:
                return (col == 0) ? p4 : (col == 1) ? p5 : p6;
            case 2:
                return (col == 0) ? p7 : (col == 1) ? p8 : p9;
            default:
                return null;
        }
    }
}