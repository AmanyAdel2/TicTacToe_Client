/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package register;

import Player.DTOPlayer;
import Player.PlayerSocket;
import Popups.PopUps;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tictactoe.TictactoeController;

/**
 * FXML Controller class
 *
 * @author HP
 */
public class RegiserController implements Initializable {

    @FXML
    private TextField userNametxt;
    @FXML
    private TextField passtxt;
    @FXML
    private TextField conftxt;
    @FXML
    private Button regBtn;
    private PlayerSocket playerSocket;
    @FXML
    private TextField emailtxt;
    @FXML
    private Button backBtn;
    private Stage stage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> userNametxt.requestFocus());
    }

    @FXML
    private void onReg(ActionEvent event) {

        playerSocket = PlayerSocket.getInstance();
        stage = (Stage) regBtn.getScene().getWindow();
        playerSocket.setStage(stage);

        String username = userNametxt.getText().trim();
        String email = emailtxt.getText().trim();
        String password = passtxt.getText().trim();
        String confirmPassword = conftxt.getText().trim();

        if (!validateInputs(username, email, password, confirmPassword)) {
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("type", "register");
        map.put("username", username);
        map.put("email", email);
        map.put("password", password);
        map.put("status", "online");

        try {
            playerSocket.sendJSON(map);
            DTOPlayer player = new DTOPlayer(username, 0, playerSocket.socket);
            playerSocket.setLoggedInPlayer(player);
            System.out.println("Registration request sent for user: " + username);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "An unexpected error occurred.");
            Logger.getLogger(RegiserController.class.getName()).log(Level.SEVERE, "Unexpected error during registration", e);
        }
    }

    @FXML
    private void onBack(ActionEvent event) {
        Stage stage = (Stage) backBtn.getScene().getWindow();
        navigateTo("/login/Login.fxml", stage);
    }

    private void navigateTo(String fxmlPath, Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            stage.setScene(new Scene(root));
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to load the requested page.");
            Logger.getLogger(RegiserController.class.getName()).log(Level.SEVERE, "Navigation error", ex);
        }
    }

    private boolean validateInputs(String username, String email, String password, String confirmPassword) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            PopUps.showErrorAlert(stage, "Registration Error", "All fields are required.");
            return false;
        }

        if (username.length() > 20) {
            showAlert(Alert.AlertType.WARNING, "Invalid Username", "Username must not exceed 20 characters.");
            return false;
        }

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.WARNING, "Invalid Email", "Please enter a valid email address.");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Password Mismatch", "Passwords do not match. Please re-enter.");
            return false;
        }

        if (password.length() < 8 || !isStrongPassword(password)) {
            showAlert(Alert.AlertType.WARNING, "Weak Password",
                    "Password must be at least 8 characters long and include an uppercase letter, a number, and a special character.");
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean isStrongPassword(String password) {
        String strongPasswordRegex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=]).+$";
        return password.matches(strongPasswordRegex);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/alert.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
        alert.showAndWait();
    }
}
