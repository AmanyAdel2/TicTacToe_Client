package tictactoe;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

public class tictactoeBase extends AnchorPane {

    protected final ImageView imageView;
    protected final ImageView imageView0;
    protected final Button locbtn;
    protected final Button combtn;
    protected final Button onbtn;

    public tictactoeBase() {

        imageView = new ImageView();
        imageView0 = new ImageView();
        locbtn = new Button();
        combtn = new Button();
        onbtn = new Button();

        setMaxHeight(USE_PREF_SIZE);
        setMaxWidth(USE_PREF_SIZE);
        setMinHeight(USE_PREF_SIZE);
        setMinWidth(USE_PREF_SIZE);
        setPrefHeight(473.0);
        setPrefWidth(475.0);

        AnchorPane.setBottomAnchor(imageView, 1.0);
        AnchorPane.setLeftAnchor(imageView, 3.0);
        AnchorPane.setRightAnchor(imageView, 2.0);
        AnchorPane.setTopAnchor(imageView, 2.0);
        imageView.setFitHeight(470.0);
        imageView.setFitWidth(500.0);
        imageView.setLayoutX(3.0);
        imageView.setLayoutY(2.0);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        imageView.setImage(new Image(getClass().getResource("../background.jpg").toExternalForm()));

        imageView0.setFitHeight(150.0);
        imageView0.setFitWidth(200.0);
        imageView0.setLayoutX(138.0);
        imageView0.setLayoutY(23.0);
        imageView0.setPickOnBounds(true);
        imageView0.setPreserveRatio(true);
        imageView0.setImage(new Image(getClass().getResource("../welcome.gif").toExternalForm()));

        locbtn.setLayoutX(138.0);
        locbtn.setLayoutY(147.0);
        locbtn.setMnemonicParsing(false);
        locbtn.setPrefHeight(48.0);
        locbtn.setPrefWidth(228.0);
        locbtn.setStyle("-fx-background-color: YELLOW;");
        locbtn.setText("LOCAL");
        locbtn.setFont(new Font("System Bold", 22.0));

        combtn.setLayoutX(138.0);
        combtn.setLayoutY(213.0);
        combtn.setMnemonicParsing(false);
        combtn.setPrefHeight(48.0);
        combtn.setPrefWidth(228.0);
        combtn.setStyle("-fx-background-color: YELLOW;");
        combtn.setText("Computer");
        combtn.setFont(new Font("System Bold", 22.0));

        onbtn.setLayoutX(138.0);
        onbtn.setLayoutY(275.0);
        onbtn.setMnemonicParsing(false);
        onbtn.setPrefHeight(48.0);
        onbtn.setPrefWidth(228.0);
        onbtn.setStyle("-fx-background-color: YELLOW;");
        onbtn.setText("Online");
        onbtn.setFont(new Font("System Bold", 22.0));

        getChildren().add(imageView);
        getChildren().add(imageView0);
        getChildren().add(locbtn);
        getChildren().add(combtn);
        getChildren().add(onbtn);

    }
}
