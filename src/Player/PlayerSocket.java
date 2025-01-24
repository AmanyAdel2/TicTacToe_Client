/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Player;

import Popups.CustomDialogController;
import Popups.PopUps;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
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
    private int score=0;
    private online.OnlineController onlineControlller;
    
    private PlayerSocket(){
        if (!isServerAvailable("127.0.0.1", 5005)) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Unable to connect!");
                alert.setContentText("Server is not available");
                alert.showAndWait();
            });
            System.out.println("Server is not available. Please start the server first.");
            return; 
        }
        try {
            socket = new Socket("127.0.0.1", 5005);
            dis = new DataInputStream(socket.getInputStream());
            ps = new PrintStream(socket.getOutputStream());
            startListening(); // Start the listener thread
        } catch (IOException ex) {
            ex.printStackTrace();
            showConnectionError();
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
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Unable to Register!");
                        alert.setContentText("The user name already exists");
                        alert.showAndWait();
                    });
                    System.out.println("registration failed");
                } 
                break;
            case "login":
                String sts = jsonMsg.get("status").toString();
                int jscore = Integer.parseInt(jsonMsg.get("score").toString());;
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
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Unable to Log in!");
                        alert.setContentText("the username or password is incorrect");
                        alert.showAndWait();
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
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Challenge Accepted");
                        alert.setContentText(challenged + " accepted your challenge!");
                        alert.showAndWait();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Challenge Declined");
                        alert.setContentText(challenged + " declined your challenge.");
                        alert.showAndWait();
                    }
                });
                break;
            case "gameStart":
                String symbol = jsonMsg.get("symbol").toString();
                String opponent = jsonMsg.get("opponent").toString();
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
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Game Over");
                    switch (result) {
                        case "win":
                            showGameOverVideo("/assets/videos/winner2.mp4", false);
                            alert.setHeaderText("Congratulations! You won!");
                            break;
                        case "lose":
                            showGameOverVideo("/assets/videos/loser.mp4", false);
                            alert.setHeaderText("Game Over! " + winner + " won the game!");
                            break;
                        case "draw":
                            showGameOverVideo("/assets/videos/draw.mp4", false);
                            alert.setHeaderText("It's a draw!");
                            break;
                    }
                    ButtonType saveButton = new ButtonType("Save Game");
                    ButtonType discardButton = new ButtonType("Discard");
                    alert.getButtonTypes().setAll(saveButton, discardButton);
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
                    if("win".equals(result)) {
                        System.out.println("Winner Score"+score);
                        setPlayerScore(score);
                    } else if ("lose".equals(result)) {
                        int pScore=getPlayerScore();
                        System.out.println("Loser Score"+pScore);
                        setPlayerScore(pScore);
                    }
                    gameController = null;
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("/online/Online.fxml"));
                        stage.setScene(new Scene(root));
                    } catch (IOException ex) {
                        Logger.getLogger(PlayerSocket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    gameController = null; 
                });
                break;
            default:
                break;
        }
    }
    
    public int getPlayerScore() {
        return score;
    }
    
    public void setPlayerScore(int jscore) {
        score=jscore;
    }
    
    public void sendJSON(Map<String, String> fields) {
        if (!isServerAvailable("127.0.0.1", 5005)) {
            System.out.println("Server is not available. Please start the server first.");
            showConnectionError();
            return; 
        }
        JSONObject data = new JSONObject();
        data.putAll(fields);
        this.ps.println(data.toJSONString());
    }
    
    public void requestReceived(String challenger){
        PopUps popup = new PopUps();
        Platform.runLater(()-> {
            String result = popup.showCustomDialog(stage, challenger);
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
    
    private void showConnectionError() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setHeaderText("Unable to Connect");
            alert.setContentText("The server is not running or unreachable. Please start the server and try again.");
            alert.showAndWait();
        });
    }
    
    private void showGameOverVideo(String videoPath, boolean isDraw) {


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