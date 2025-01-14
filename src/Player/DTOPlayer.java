/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Player;

import java.net.Socket;

/**
 *
 * @author Mohamed Sameh
 */
public class DTOPlayer {
    private int playerId;
    private String username;
    private String password;
    private String email;
    private String status; // "available" or "in game"
    private int score;
    private Socket socket;
    
    public DTOPlayer(String username, String status, int score, Socket socket) {
        this.username = username;
        this.status = status;
        this.score = score;
        this.socket = socket;
    }
    
    public DTOPlayer() {
        this("", "", "", "offline", 0, null);
    }
    public DTOPlayer(String username,String password,String email, String status, int score, Socket socket) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.status = status;
        this.score = score;
        this.socket = socket;
    }

    public String getEmail() {
        return email;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
   

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    
}
