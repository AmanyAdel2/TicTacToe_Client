/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Player;

import Popups.CustomDialogController;
import Popups.PopUps;
import Popups.PopUps.Type;
import game.GameController;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tictactoe.TicTacToe;

public class PlayerSocket {
    public Socket socket;
    public DataInputStream dis;
    public PrintStream ps;
    private JSONObject jsonMsg;
    
    private ObservableSet<String> onlinePlayers = FXCollections.observableSet();
    private static PlayerSocket instance; // Singleton instance
    
    private DTOPlayer loggedInPlayer;
    private GameController gameController;  // Each socket has its own controller
    
    private Stage stage;
    private boolean running = true; 
    private int score = 0;
    private int otherScore = 0;
    private PopUps popup;

    
    private PlayerSocket(){
        popup = new PopUps();
        try {
            socket = new Socket("127.0.0.1", 5005);
            dis = new DataInputStream(socket.getInputStream());
            ps = new PrintStream(socket.getOutputStream());
            startListening(); // Start the listener thread
        } catch (IOException ex) {
            PopUps.showErrorAlert(stage, "Connection Error", "Server is not available");
        }
    }
    
    public static synchronized PlayerSocket getInstance() {
        if (instance == null) {
            instance = new PlayerSocket();
        }
        return instance;
    }
       
    private void startListening() {
        Thread listenerThread = new Thread(() -> {
            try {
                while(running) {
                    try {
                        String data = dis.readLine();
                        if (data == null) {
                            System.out.println("Server connection closed");
                            break;
                        }

                        if (!data.isEmpty()) {
                            handleJSON(data);
                        }
                    } catch (IOException e) {
                        if (running) {
                            System.out.println("Connection error: " + e.getMessage());
                        }
                        break;
                    }
                }
            } catch (ParseException e) {
                System.out.println("JSON parsing error: " + e.getMessage());
            } finally {
                closeSocket();
            }
        });
        listenerThread.start();
    }  
    
    private void handleJSON(String data) throws ParseException{
        JSONParser parser = new JSONParser();
        jsonMsg = (JSONObject) parser.parse(data);
            
        switch(jsonMsg.get("type").toString()){
            case "register":
                String res = jsonMsg.get("status").toString();
                int rScore = jsonMsg.get("score") != null
                        ?Integer.parseInt(jsonMsg.get("score").toString()):0;
                setPlayerScore(rScore);
                if(res.equals("1")){
                    System.out.println("Registered successfully");
                    Platform.runLater(() -> {               
                        try {
                            Parent root = FXMLLoader.load(getClass().getResource("/online/Online.fxml"));
                            stage.setScene(new Scene(root));
                        } catch (IOException ex) {
                            Logger.getLogger(PlayerSocket.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                }else{
                    Platform.runLater(() -> {
                        PopUps.showErrorAlert(stage, "Registeration Error", "The user name already exists");
                    });
                    System.out.println("registration failed");
                } 
                break;
            case "login":
                String sts = jsonMsg.get("status").toString();
                int jscore = jsonMsg.get("score") != null
                        ?Integer.parseInt(jsonMsg.get("score").toString()):0;
                
                setPlayerScore(jscore);

                if(sts.equals("1")){
                    System.out.println("Logged in successfully");
                    Platform.runLater(() -> {               
                        try {
                            Parent root = FXMLLoader.load(getClass().getResource("/online/Online.fxml"));
                            stage.setScene(new Scene(root));
                        } catch (IOException ex) {
                            Logger.getLogger(PlayerSocket.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                } else {
                    Platform.runLater(() -> {
                        PopUps.showErrorAlert(stage, "Login Error", "the username or password is incorrect");
                    });
                    System.out.println("Log in failed");
                } 
                break;
            case "onlinePlayers":
                final JSONArray players = (JSONArray) jsonMsg.get("players");
                Platform.runLater(() -> {
                    if (onlinePlayers == null || players == null || loggedInPlayer == null || loggedInPlayer.getUsername() == null) {
                        System.out.println("Error: onlinePlayers, players, or loggedInPlayer is null");
                        return;
                    }
                    System.out.println("Updating online players list: " + players.toJSONString());
                    Set<String> tempSet = new HashSet<>();
                    for (Object player : players) {
                        String username = player.toString();
                        if (!username.equals(loggedInPlayer.getUsername())) {
                            tempSet.add(username);
                        }
                    }
                    onlinePlayers.clear();
                    onlinePlayers.addAll(tempSet);
                });
                break;
            case "receiveGameReq":
                String challenger = jsonMsg.get("challenger").toString();
                requestReceived(challenger);
                break;
            case "gameReqResult":
                final String status = jsonMsg.get("status").toString();
                final String challenged = jsonMsg.get("challenged").toString();
                
                Platform.runLater(() -> {
                    if (status.equals("accepted")) {
                        
                        popup.showCustomDialog(stage, challenged, "Challenge Accepted", "accepted your challenge!", Type.RESULT);
                    } else {
                        popup.showCustomDialog(stage, challenged, "Challenge Declined", "declined your challenge.", Type.RESULT);
                    }
                });
                break;
            case "serverDisconnection":
                System.out.println("Server is not available");
                Platform.runLater(() -> {
                    PopUps.showErrorAlert(stage, "Connection Error", "Server is not available");
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("/login/Login.fxml"));
                        stage.setScene(new Scene(root));
                    } catch (IOException ex) {
                            Logger.getLogger(PlayerSocket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                break;
            case "gameStart":
                String symbol = jsonMsg.get("symbol").toString();
                String opponent = jsonMsg.get("opponent").toString();
                final int playerScore= jsonMsg.get("score") != null
                        ?Integer.parseInt(jsonMsg.get("score").toString()):getPlayerScore();
                setOtherPlayerScore(playerScore);
                
                Platform.runLater(() -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/Game.fxml"));
                        Parent root = loader.load();
                        gameController = loader.getController();
                        gameController.initializeGame(symbol, opponent);
                        stage.setScene(new Scene(root));
                    } catch (IOException ex) {
                        Logger.getLogger(PlayerSocket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                break;
            case "gameMove":
                
                System.out.println("Received gameMove message: " + jsonMsg.toJSONString());
                int row, col;
                String symbolRec;   
                if (gameController == null) {
                    System.out.println("Warning: Received move but gameController is null");
                    return;
                }
                try{
                    row = Integer.parseInt(jsonMsg.get("row").toString());
                    col = Integer.parseInt(jsonMsg.get("col").toString());
                    symbolRec = jsonMsg.get("symbol").toString();
                    final int finalRow = row;
                    final int finalCol = col;
                    final String finalSymbol = symbolRec;
                    Platform.runLater(() -> {
                        try {
                            if (gameController != null) {
                                gameController.updateBoard(finalRow, finalCol, finalSymbol);
                            } else {
                                System.out.println("Warning: gameController became null before update");
                            }
                        } catch (Exception e) {
                            System.out.println("Error updating board: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                }catch(Exception e){
                    System.out.println("Error processing game move: " + e.getMessage());
                }
                break;

            case "opponentDisconnected":
                System.out.println("type is " + jsonMsg.get("type").toString());
                
                if (gameController != null) {
                    gameController.deleteTemporaryFile();
                }
                
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Game Over");
                    alert.setContentText("Your opponent has disconnected. Returning to the home screen.");
                    alert.initOwner(stage);
                    alert.showAndWait();
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("/online/Online.fxml"));
                        stage.setScene(new Scene(root));
                    } catch (IOException ex) {
                        Logger.getLogger(PlayerSocket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                break;
            case "gameEnd":
                final String result = jsonMsg.get("result").toString();
                final String winner = jsonMsg.get("winner") != null ? jsonMsg.get("winner").toString() : null;
                final int score= jsonMsg.get("score") != null ? Integer.parseInt(jsonMsg.get("score").toString()) : 0;
                
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.NONE);
                    alert.setTitle("Game Over");
                    switch (result) {
                        case "win":
                            showGameOverVideo("/assets/videos/winner2.mp4", stage);
                            alert.setHeaderText("🎉 Congratulations! You won!");
                            alert.getDialogPane().setStyle("-fx-background-color: #d4edda; -fx-border-color: #155724; -fx-border-width: 2px;");
                            alert.getDialogPane().lookup(".header-panel").setStyle("-fx-font-size: 18px; -fx-text-fill: #155724;");
                            alert.getDialogPane().lookup(".header-panel").setStyle(
                                "-fx-background-color: #5bde7e;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 16px;" +
                                "-fx-background-radius: 10px;" +
                                "-fx-padding: 10px;"
                            );
                            break;
                        case "lose":
                            showGameOverVideo("/assets/videos/loser.mp4", stage);
                            alert.setHeaderText("💔 Game Over! " + winner + " won the game!");
                            alert.getDialogPane().setStyle("-fx-background-color: #f8d7da; -fx-border-color: #721c24; -fx-border-width: 2px;");
                            alert.getDialogPane().lookup(".header-panel").setStyle("-fx-font-size: 18px; -fx-text-fill: #721c24;");
                            alert.getDialogPane().lookup(".header-panel").setStyle(
                                "-fx-background-color: #FF6B6B;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 16px;" +
                                "-fx-background-radius: 10px;" +
                                "-fx-padding: 10px;"
                            );
                            break;
                        case "draw":
                            showGameOverVideo("/assets/videos/draw.mp4", stage);
                            alert.setHeaderText("🤝 It's a draw!");
                            alert.getDialogPane().setStyle("-fx-background-color: #fff3cd; -fx-border-color: #856404; -fx-border-width: 2px;");
                            alert.getDialogPane().lookup(".header-panel").setStyle("-fx-font-size: 18px; -fx-text-fill: #856404;");
                            break;
                    }
                    
                    ButtonType saveButton = new ButtonType("💾 Save Game");
                    ButtonType discardButton = new ButtonType("🗑️ Discard");
                    alert.getButtonTypes().setAll(saveButton, discardButton);
                    alert.initOwner(stage);
                    Optional<ButtonType> choice = alert.showAndWait();
                    if (choice.isPresent()) {
                        if (choice.get() == saveButton) {
                            if (gameController != null) {
                                gameController.moveFileToGameHistory();
                                System.out.println("Game saved successfully.");
                            }
                        } else {
                            if (gameController != null) {
                                gameController.deleteTemporaryFile();
                                System.out.println("Game discarded.");
                            }
                        }
                    }
                   if("win".equals(result))
                    {                        
                        setPlayerScore(score);
                    }
                    else if ("lose".equals(result))
                    {
                        int pscore=getPlayerScore();
                        setPlayerScore(pscore);
                         
                    }
                   
                    gameController = null;
                    
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("/online/Online.fxml"));
                        stage.setScene(new Scene(root));
                    } catch (IOException ex) {
                        Logger.getLogger(PlayerSocket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                break;
            default:
                break;
        }
    }
    
    public int getPlayerScore() {
      return score;
    }
    public int getOtherPlayerScore() {
        return otherScore;
    }

    public void setPlayerScore(int jscore) {

        score=jscore;
    }
    public void setOtherPlayerScore(int jscore) {

      otherScore=jscore;
    }

    public void sendJSON(Map<String, String> fields) {
        if (!isServerAvailable("127.0.0.1", 5005)) {
            System.out.println("Server is not available. Please start the server first.");
            PopUps.showErrorAlert(stage, "Connection Error", "Server is not available");
            return; 
        }
        JSONObject data = new JSONObject();
        data.putAll(fields);
        this.ps.println(data.toJSONString());
    }
    
    public void requestReceived(String challenger){
        
        Platform.runLater(()-> {
            String result = popup.showCustomDialog(stage, challenger, "Game Challenge", "wants to play against you",Type.CHALLENGE);
            Map<String, String> response = new HashMap<>();
            response.put("type", "gameReqResponse");
            response.put("challenger", challenger);
            response.put("challenged", loggedInPlayer.getUsername());
            System.out.println("result is "+ result);
            if (result != null && result.equals("Accepted")) {
                System.out.println("Challenge accepted!");
                response.put("status", "accepted");
            } else {
                System.out.println("Challenge declined.");
                response.put("status", "declined");
            }
            sendJSON(response);
        });
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }
    
    public ObservableSet<String> getOnlinePlayers() {
        return onlinePlayers;
    }
    
    public DTOPlayer getLoggedInPlayer() {
        return loggedInPlayer;
    }

    public void setLoggedInPlayer(DTOPlayer loggedInPlayer) {
        this.loggedInPlayer = loggedInPlayer;
    }
    
    private boolean isServerAvailable(String host, int port) {
        try (Socket testSocket = new Socket(host, port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    private void showGameOverVideo(String videoPath, Stage parentStage) {

        boolean wasMusicPlaying = TicTacToe.mediaPlayer != null && TicTacToe.mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;

        if (wasMusicPlaying) {
            TicTacToe.mediaPlayer.pause();
        }

        Stage videoStage = new Stage();
        Media media = new Media(getClass().getResource(videoPath).toString());
        MediaPlayer videoPlayer = new MediaPlayer(media);
        videoPlayer.setVolume(1.0);

        MediaView mediaView = new MediaView(videoPlayer);
        mediaView.setPreserveRatio(true);
        mediaView.fitWidthProperty().bind(videoStage.widthProperty());
        mediaView.fitHeightProperty().bind(videoStage.heightProperty());

        StackPane videoRoot = new StackPane();
        videoRoot.getChildren().add(mediaView);
        Scene videoScene = new Scene(videoRoot, 600, 600);
        videoStage.setScene(videoScene);
        videoStage.setTitle("Game Over");

        // Set modality and owner
        videoStage.initModality(Modality.WINDOW_MODAL);
        videoStage.initOwner(parentStage);

        // Center the video stage relative to the parentStage
        videoStage.setOnShowing(event -> {
            double centerXPosition = parentStage.getX() + parentStage.getWidth() / 2 - videoStage.getWidth() / 2;
            double centerYPosition = parentStage.getY() + parentStage.getHeight() / 2 - videoStage.getHeight() / 2;
            videoStage.setX(centerXPosition);
            videoStage.setY(centerYPosition);
        });

        videoStage.setOnCloseRequest(event -> {
            videoPlayer.stop();
            videoStage.close();

            if (wasMusicPlaying && TicTacToe.mediaPlayer != null) {
                TicTacToe.mediaPlayer.play();
            }

            event.consume();
        });

        videoStage.show();
        videoPlayer.play();
    }

    private void showErrorAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);

    // Apply custom style
    DialogPane dialogPane = alert.getDialogPane();
    dialogPane.getStylesheets().add(getClass().getResource("/styles/alert.css").toExternalForm());
    dialogPane.getStyleClass().add("custom-alert");

    alert.showAndWait();
}


    public void closeSocket() {
        running = false;
        try {
            if (ps != null) ps.close();
            if (dis != null) dis.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}