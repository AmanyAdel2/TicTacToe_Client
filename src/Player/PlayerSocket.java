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
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
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

    private  PlayerSocket(){
        try {
            socket = new Socket("127.0.0.1", 5005);
            dis = new DataInputStream(socket.getInputStream());
            ps = new PrintStream(socket.getOutputStream());
            startListening(); // Start the listener thread
        } catch (IOException ex) {
            System.out.println("error2");
            ex.printStackTrace();
        }
    
    }
    
     // Public method to get the Singleton instance
    public static synchronized PlayerSocket getInstance() {
        if (instance == null) {
            instance = new PlayerSocket();
        }
        return instance;
    }
    
    public ObservableSet<String> getOnlinePlayers() {
        return onlinePlayers;
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
                    System.out.println("Disconnected from server or error occurred");
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
                }else System.out.println("failed");
                
                break;
            case "login":
//              createPlayer();
                break;
                
            case "onlinePlayers":
                JSONArray players = (JSONArray) jsonMsg.get("players");
                
                Platform.runLater(() -> {
                   onlinePlayers.clear(); // Clear the list before updating
                   for (Object player : players) {
                       System.out.println(player.toString());
                       onlinePlayers.add(player.toString());
                   }
               });
                break;
            }
       
       }
    
    public void sendJSON(Map<String, String> fields) {
        
        JSONObject data = new JSONObject();
        data.putAll(fields);
        System.out.println(data.get("username").toString());
        this.ps.println(data.toJSONString());
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
