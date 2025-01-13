/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package localMode;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.layout.StackPane;

public class LocalModeController implements Initializable {

    @FXML
    private Text player1Label;

    @FXML
    private Text player2Label;

    @FXML
    private Button p1, p2, p3, p4, p5, p6, p7, p8, p9;

    private GameLocalLogic logic = new GameLocalLogic();
    private int player1Score = 0;
    private int player2Score = 0;
    private String player1 = "Player 1";
    private String player2 = "Player 2";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resetGame();
    }

    public void setPlayerNames(String player1, String player2) {
        this.player1 = (player1 == null || player1.isEmpty()) ? "Guest 1" : player1;
        this.player2 = (player2 == null || player2.isEmpty()) ? "Guest 2" : player2;
        player1Label.setText(this.player1);
        player2Label.setText(this.player2);
    }

    private void handleButtonPress(Button button) {
        int index = Integer.parseInt(button.getId().substring(1)) - 1; 
        int row = index / 3;
        int col = index % 3;

        if (logic.makeMove(row, col)) {
            String currentPlayer = String.valueOf(logic.getCurrentPlayer());
            button.setText(currentPlayer);
            
            if (logic.getCurrentPlayer() == 'X') {
                button.setStyle("-fx-text-fill: red; -fx-font-size: 20; -fx-font-weight: bold;"); 
            } else {
                button.setStyle("-fx-text-fill: blue; -fx-font-size: 20; -fx-font-weight: bold;");
            }

            if (logic.checkWinner()) {
                String winner = logic.getCurrentPlayer() == 'X' ? player1 : player2;
                showGameOverVideo("winner1.mp4", winner + " Wins!", false); // تمرير false للفوز
                if (winner.equals(player1)) {
                    player1Score++;
                } else {
                    player2Score++;
                }
                updateScores();
                return;
            }
            if (logic.isBoardFull()) {
                showGameOverVideo("draw.mp4", "It's a Draw!", true); // تمرير true للتعادل
                return;
            }
            logic.switchPlayer();
        }
    }

    private void showGameOverVideo(String videoPath, String message, boolean isDraw) {
        Stage videoStage = new Stage();
        Media media = new Media(getClass().getResource(videoPath).toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(1.0); // تأكد من أن الصوت شغال
        MediaView mediaView = new MediaView(mediaPlayer);

        StackPane videoRoot = new StackPane();
        videoRoot.getChildren().add(mediaView);
        
        // تحديد أبعاد الفيديو حسب الحالة
        Scene videoScene = new Scene(videoRoot, isDraw ? 800 : 600, isDraw ? 800 : 400);
        videoStage.setScene(videoScene);
        videoStage.setTitle("Game Over");

        // استماع لإغلاق نافذة الفيديو
        videoStage.setOnCloseRequest(event -> {
            mediaPlayer.stop(); // إيقاف الفيديو
            videoStage.close(); // إغلاق نافذة الفيديو
            showGameOverAlert(message); // عرض رسالة النتيجة
            event.consume(); // منع الإغلاق الافتراضي
        });

        videoStage.show();
        mediaPlayer.play();
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
        if (message.contains("Player 1")) {
            alert.getDialogPane().setStyle(
                "-fx-background-color: lightblue;" +
                "-fx-font-size: 16;" +
                "-fx-font-weight: bold;"
            );
        } else if (message.contains("Player 2")) {
            alert.getDialogPane().setStyle(
                "-fx-background-color: lightpink;" +
                "-fx-font-size: 16;" +
                "-fx-font-weight: bold;"
            );
        }
        Node content = alert.getDialogPane().lookup(".content");
        if (content != null) {
            content.setStyle("-fx-background-color: beige;");
        }

        alert.showAndWait().ifPresent(response -> {
            if (response == playAgainButton) {
                resetGame();
            } else {
                player1Score = 0;
                player2Score = 0;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/playersName/PlayersName.fxml"));
            Stage stage = (Stage) p1.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateScores() {
        player1Label.setText(player1 + " (" + player1Score + ")");
        player2Label.setText(player2 + " (" + player2Score + ")");
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
}