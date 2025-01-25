/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class TictactoeController implements Initializable {

    @FXML
    private Button locbtn;
    @FXML
    private Button combtn;
    @FXML
    private Button onbtn;
    @FXML
    private ImageView soundOnIcon; // أيقونة تشغيل الصوت
    @FXML
    private ImageView soundOffIcon; // أيقونة إيقاف الصوت

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // إضافة معالجات الأحداث للأيقونات
        soundOnIcon.setOnMouseClicked(event -> {
            TicTacToe.mediaPlayer.play(); // تشغيل الصوت
        });

        soundOffIcon.setOnMouseClicked(event -> {
            TicTacToe.mediaPlayer.pause(); // إيقاف الصوت
        });
    }

    @FXML
    void goLevel(MouseEvent event) {
        try {
            Stage stage = (Stage) combtn.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/level/Level.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException ex) {
            Logger.getLogger(TictactoeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void gonline(MouseEvent event) {
        try {
            Stage stage = (Stage) onbtn.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/login/Login.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException ex) {
            Logger.getLogger(TictactoeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void golocalgame(MouseEvent event) throws IOException {
        Stage stage = (Stage) locbtn.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/playersName/PlayersName.fxml"));
        stage.setScene(new Scene(root));
    }
}