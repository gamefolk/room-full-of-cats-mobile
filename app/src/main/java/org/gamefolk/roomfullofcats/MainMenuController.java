package org.gamefolk.roomfullofcats;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {
    @FXML private HBox settings;
    @FXML private BorderPane root;
    @FXML private ImageView logo;

    @FXML
    private void startGame(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game.fxml"));
        Parent game;
        try {
            game = loader.load();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        stage.getScene().setRoot(game);

        // Ensure that the canvas is the correct size before starting the game.
        stage.getScene().getRoot().layout();
        ((GameController) loader.getController()).startGame();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (Node checkBox : settings.getChildrenUnmodifiable()) {
            HBox.setHgrow(checkBox, Priority.ALWAYS);
        }

        logo.fitWidthProperty().bind(root.widthProperty());
    }
}
