/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Player;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Mohamed Sameh
 */
public class PlayerSocket {
    public Socket socket;
    public DataInputStream dis;
    public PrintStream ps;
    private JSONObject jsonMsg;
    private ObservableSet<String> onlinePlayers = FXCollections.observableSet();
    private static PlayerSocket instance; // Singleton instance
    private DTOPlayer loggedInPlayer;

    private Stage stage;
    
    public PlayerSocket(){
        
        if (!isServerAvailable("127.0.0.1", 5005)) {
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
     // Public method to get the Singleton instance
    public static synchronized PlayerSocket getInstance() {
        if (instance == null) {
            instance = new PlayerSocket();
        }
        return instance;
    }
       
    private void startListening() {
        Thread listenerThread = new Thread(() -> {
            while (!socket.isClosed()) {
                try {
                    String data = dis.readLine();
                    if (data != null && !data.isEmpty()) {
                        handleJSON(data);
                    }
                } catch (IOException | ParseException e) {
                    System.out.println("Disconnected from server");
                    closeSocket();
                    break; // Exit the loop when the socket is closed
                }
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
                System.out.println("Resgistered successfully");
                Platform.runLater(() -> {               
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("/online/Online.fxml"));
                        stage.setScene(new Scene(root));
                        
                    } catch (IOException ex) {
                        Logger.getLogger(PlayerSocket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                }else System.out.println("failed");
                
                break;
            case "login":
//              createPlayer();
                break;
                
            case "onlinePlayers":
                JSONArray players = (JSONArray) jsonMsg.get("players");
                
                Platform.runLater(() -> {
                   onlinePlayers.clear(); 
                   for (Object player : players) {
                       String username = player.toString();
                       if (!username.equals(loggedInPlayer.getUsername())) {
                            onlinePlayers.add(username);
                        }
                       
                   }
               });
                break;
                
            case "receiveGameReq":
                String challenger = jsonMsg.get("challenger").toString();
                requestReceived(challenger);
                break;
              
            }
       
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
        Platform.runLater(()-> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Challenge");
            alert.setContentText("do you want to play against  " + challenger);
            alert.showAndWait();
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

    public void closeSocket() {
        try {
            if (ps != null) ps.close();
            if (dis != null) dis.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
