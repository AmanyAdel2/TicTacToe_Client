/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intermediateGame;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.layout.StackPane;
import tictactoe.TicTacToe;

public class IntermediateGameController implements Initializable {

    @FXML
    private Text playerLabel;

    @FXML
    private Text computerLabel;

    @FXML
    private Button p1, p2, p3, p4, p5, p6, p7, p8, p9;

    private IntermediateGameLogic logic = new IntermediateGameLogic();
    private int playerScore = 0;
    private int computerScore = 0;
    private String player = "Player";
    private String computer = "Computer";
    private String gameResult = "";
    private boolean gameEnded = false;
    private String currentGameFileName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String baseFileName = "game_records/" + player + "_vs_" + computer + " Intermediate Level" + ".txt";
        currentGameFileName = getUniqueFileName(baseFileName);
        ensureGameRecordsFolderExists();
        resetGame();
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

        return newFileName;
    }

    private void ensureGameRecordsFolderExists() {
        File folder = new File("game_records");
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    private void saveMoveToFile(String move) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentGameFileName, true))) {
            writer.write(move + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleButtonPress(Button button) {
        if (gameEnded) return;

        int index = Integer.parseInt(button.getId().substring(1)) - 1;
        int row = index / 3;
        int col = index % 3;

        if (logic.makeMove(row, col, 'X')) {
            button.setText('X' + "");
            button.setStyle("-fx-text-fill: red; -fx-font-size: 45; -fx-font-weight: bold;");
            saveMoveToFile("X " + (index + 1));

            List<int[]> winningCells = logic.checkWinner('X');
            if (!winningCells.isEmpty()) {
                highlightWinningCells(winningCells, 'X'); 
                gameResult = "Player Wins!";
                saveMoveToFile(gameResult);
                showGameOverVideo("/assets/videos/winner2.mp4", false);
                playerScore++;
                updateScores();
                return;
            }
            if (logic.isBoardFull()) {
                gameResult = "It's a Draw!";
                saveMoveToFile(gameResult);
                showGameOverVideo("/assets/videos/draw.mp4", true);
                return;
            }

            computerMove();
        }
    }

    private void computerMove() {
        if (gameEnded) return;

        int[] move = logic.findBestMove('O');
        if (move != null) {
            int row = move[0];
            int col = move[1];
            logic.makeMove(row, col, 'O');

            Button button = getButtonByRowCol(row, col);
            if (button != null) {
                button.setText('O' + "");
                button.setStyle("-fx-text-fill: blue; -fx-font-size: 45; -fx-font-weight: bold;");
                saveMoveToFile("O " + ((row * 3) + col + 1));
            }

            List<int[]> winningCells = logic.checkWinner('O');
            if (!winningCells.isEmpty()) {
                highlightWinningCells(winningCells, 'O');
                gameResult = "Computer Wins!";
                saveMoveToFile(gameResult);
                showGameOverVideo("/assets/videos/lose2.mp4", false);
                computerScore++;
                updateScores();
            } else if (logic.isBoardFull()) {
                gameResult = "It's a Draw!";
                saveMoveToFile(gameResult);
                showGameOverVideo("/assets/videos/draw.mp4", true);
            }
        }
    }

    private void highlightWinningCells(List<int[]> winningCells, char player) {
        String color = (player == 'X') ? "red" : "blue"; 
        for (int[] cell : winningCells) {
            int row = cell[0];
            int col = cell[1];
            Button button = getButtonByRowCol(row, col);
            if (button != null) {
                button.setStyle(
                    "-fx-background-color: lightgreen; " + 
                    "-fx-text-fill: " + color + "; " + 
                    "-fx-font-size: 45; " +
                    "-fx-font-weight: bold;"
                );
            }
        }
    }

    private void showGameOverVideo(String videoPath, boolean isDraw) {
        gameEnded = true;

        
        if (TicTacToe.mediaPlayer != null) {
            TicTacToe.mediaPlayer.pause();
        }

        Stage videoStage = new Stage();
        Media media = new Media(getClass().getResource(videoPath).toString());
        MediaPlayer videoPlayer = new MediaPlayer(media);
        videoPlayer.setVolume(1.0);
        MediaView mediaView = new MediaView(videoPlayer);

        StackPane videoRoot = new StackPane();
        videoRoot.getChildren().add(mediaView);

        Scene videoScene = new Scene(videoRoot, isDraw ? 800 : 550, isDraw ? 600 : 550);
        videoStage.setScene(videoScene);
        videoStage.setTitle("Game Over");

        videoStage.setOnCloseRequest(event -> {
            videoPlayer.stop(); 
            videoStage.close(); 

            
            if (TicTacToe.mediaPlayer != null) {
                TicTacToe.mediaPlayer.play();
            }

            showGameOverAlert(gameResult); 
            event.consume();
        });

        videoStage.show();
        videoPlayer.play(); 
    }

    private void showGameOverAlert(String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(message);
        alert.setContentText("Choose your next action:");

        ButtonType playAgainButton = new ButtonType("Play Again");
        ButtonType saveGameButton = new ButtonType("Save Game");
        ButtonType backButton = new ButtonType("Back");
        alert.getButtonTypes().setAll(playAgainButton, saveGameButton, backButton);
        alert.setGraphic(null);
        alert.getDialogPane().setStyle(
                "-fx-background-color: beige;"
                + "-fx-font-size: 16;"
                + "-fx-font-weight: bold;"
        );
        alert.getDialogPane().lookupButton(playAgainButton).setStyle(
                "-fx-background-color: lightgreen;"
                + "-fx-font-size: 14;"
                + "-fx-font-weight: bold;"
        );
        alert.getDialogPane().lookupButton(backButton).setStyle(
                "-fx-background-color: lightcoral;"
                + "-fx-font-size: 14;"
                + "-fx-font-weight: bold;"
        );
        alert.getDialogPane().lookupButton(saveGameButton).setStyle(
                "-fx-background-color: yellow;"
                + "-fx-font-size: 14;"
                + "-fx-font-weight: bold;"
        );

        alert.showAndWait().ifPresent(response -> {
            if (response == playAgainButton) {
                deleteTemporaryFile();
                resetGame();
            } else if (response == backButton) {
                deleteTemporaryFile();
                playerScore = 0;
                computerScore = 0;
                updateScores();
                goToBackScene();
            } else if (response == saveGameButton) {
                moveFileToGameHistory();
                Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
                confirmAlert.setTitle("Game Saved");
                confirmAlert.setHeaderText("Game has been saved successfully!");
                confirmAlert.setContentText("What would you like to do next?");

                ButtonType playAgainAfterSaveButton = new ButtonType("Play Again");
                ButtonType backAfterSaveButton = new ButtonType("Back");
                confirmAlert.getButtonTypes().setAll(playAgainAfterSaveButton, backAfterSaveButton);
                confirmAlert.setGraphic(null);
                confirmAlert.getDialogPane().setStyle(
                        "-fx-background-color: beige;"
                        + "-fx-font-size: 16;"
                        + "-fx-font-weight: bold;"
                );
                confirmAlert.getDialogPane().lookupButton(playAgainAfterSaveButton).setStyle(
                        "-fx-background-color: lightgreen;"
                        + "-fx-font-size: 14;"
                        + "-fx-font-weight: bold;"
                );
                confirmAlert.getDialogPane().lookupButton(backAfterSaveButton).setStyle(
                        "-fx-background-color: lightcoral;"
                        + "-fx-font-size: 14;"
                        + "-fx-font-weight: bold;"
                );

                confirmAlert.showAndWait().ifPresent(confirmResponse -> {
                    if (confirmResponse == playAgainAfterSaveButton) {
                        resetGame();
                    } else if (confirmResponse == backAfterSaveButton) {
                        playerScore = 0;
                        computerScore = 0;
                        updateScores();
                        goToBackScene();
                    }
                });
            }
        });
    }
    private void moveFileToGameHistory() {
        File file = new File(currentGameFileName);
        if (file.exists()) {
            File destination = new File("game_records/" + file.getName());
            if (file.renameTo(destination)) {
                System.out.println("File moved to game_records folder.");
            } else {
                System.out.println("Failed to move the file.");
            }
        }
    }

    private void deleteTemporaryFile() {
        File file = new File(currentGameFileName);
        if (file.exists()) {
            file.delete();
        }
    }

    @FXML
    private void openRecord(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/easyGame/GameRecord.fxml"));
            AnchorPane root = loader.load();

            Stage recordStage = new Stage();
            recordStage.setTitle("Game Moves");
            recordStage.setScene(new Scene(root, 664, 664));
            recordStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Unable to open the game record.");
            errorAlert.setContentText("Please check if the record file exists.");
            errorAlert.showAndWait();
        }
    }

    private void resetGame() {
        logic.resetGame();
        for (Button button : new Button[]{p1, p2, p3, p4, p5, p6, p7, p8, p9}) {
            button.setText("");
            button.setStyle("-fx-background-color: beige; -fx-font-size: 14; -fx-font-weight: bold;");
            button.setOnAction(e -> handleButtonPress(button));
        }
        gameEnded = false;
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
        alert.setGraphic(null);
        alert.getDialogPane().setStyle(
            "-fx-background-color: beige;" +
            "-fx-font-size: 16;" +
            "-fx-font-weight: bold;"
        );
        alert.getDialogPane().lookupButton(yesButton).setStyle(
            "-fx-background-color: lightgreen;" +
            "-fx-font-size: 14;" +
            "-fx-font-weight: bold;"
        );
        alert.getDialogPane().lookupButton(noButton).setStyle(
            "-fx-background-color: lightcoral;" +
            "-fx-font-size: 14;" +
            "-fx-font-weight: bold;"
        );

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            goToBackScene();
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