package playersName;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Dell
 */
public class PlayersNameController {

    @FXML
    private TextField player1Field;

    @FXML
    private TextField player2Field;

    @FXML
    private void startGame(ActionEvent event) throws IOException {

        String player1Name = player1Field.getText().isEmpty() ? "Guest 1" : player1Field.getText();
        String player2Name = player2Field.getText().isEmpty() ? "Guest 2" : player2Field.getText();


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/localMode/LocalMode.fxml"));
        Parent root = loader.load();

        localMode.LocalModeController controller = loader.getController();
        controller.setPlayerNames(player1Name, player2Name);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
       // stage.setTitle="";
        stage.show();
    }
    @FXML
    private void bkButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tictactoe/tictactoe.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
    
}