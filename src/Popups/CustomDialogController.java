package Popups;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author DeSkToP
 */
public class CustomDialogController implements Initializable {

    @FXML
    private Label titleLabel;
    @FXML
    private Label messageLabel;
    @FXML
    private Button cancelButton;
    @FXML
    private Button okButton;

    private Runnable onOkAction;
    private Runnable onCancelAction;

    private final ObjectProperty<String> result = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Bind button actions
        okButton.setOnAction(event -> handleOk());
        cancelButton.setOnAction(event -> handleCancel());
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    public void setOnOkAction(Runnable action) {
        this.onOkAction = action;
    }

    public void setOnCancelAction(Runnable action) {
        this.onCancelAction = action;
    }

    private void handleOk() {
        if (onOkAction != null) {
            onOkAction.run();
        }
        result.set("Accepted"); // Set result to "Accepted"
        closeWindow();
    }

    private void handleCancel() {
        if (onCancelAction != null) {
            onCancelAction.run();
        }
        result.set("declined"); // Set result to "declined"
        closeWindow();
    }

    private void closeWindow() {
        okButton.getScene().getWindow().hide();
    }

    public String getResult() {
        return result.get();
    }
    
     public void configureButtons(PopUps.Type type) {
        if (type == PopUps.Type.RESULT) {
            // Show only the OK button
            okButton.setVisible(true);
            cancelButton.setVisible(false);
            okButton.setText("OK");
        } else if (type == PopUps.Type.CHALLENGE) {
            // Show both buttons
            okButton.setVisible(true);
            cancelButton.setVisible(true);
            okButton.setText("Accept");
        }
    }
    public ObjectProperty<String> resultProperty() {
        return result;
    }
}