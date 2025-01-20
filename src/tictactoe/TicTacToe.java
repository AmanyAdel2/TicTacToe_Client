/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;
import Player.PlayerSocket;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TicTacToe extends Application {

    @Override
   
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/tictactoe/tictactoe.fxml"));
            System.out.println(getClass().getResource("/tictactoe.fxml"));
            
            primaryStage.setScene(new Scene(root));
            primaryStage.setOnCloseRequest(event -> {
                System.out.println("Application is closing...");
                PlayerSocket.getInstance().closeSocket();
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
