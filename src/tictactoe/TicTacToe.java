/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;

import Player.PlayerSocket;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class TicTacToe extends Application {

    public static MediaPlayer mediaPlayer; // MediaPlayer كمتغير ثابت

    @Override
    public void start(Stage primaryStage) {
        try {
            // تحميل ملف الصوت
            String musicFile = getClass().getResource("/assets/sounds/Music.mp3").toExternalForm();
            Media media = new Media(musicFile);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // تكرار الصوت بشكل مستمر
            mediaPlayer.play(); // تشغيل الصوت تلقائيًا عند بدء التطبيق

            // تحميل واجهة المستخدم
            Parent root = FXMLLoader.load(getClass().getResource("/tictactoe/tictactoe.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.setOnCloseRequest(event -> {
                System.out.println("Application is closing...");
                PlayerSocket.getInstance().closeSocket();
                mediaPlayer.stop(); // إيقاف الصوت عند إغلاق التطبيق
                System.exit(0);
            });
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}