package org.gamefolk.roomfullofcats;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainMenuController implements Initializable {
    @FXML private HBox settingsRegion;
    @FXML private BorderPane root;
    @FXML private ImageView logo;
    @FXML private CheckBox playMusic;
    @FXML private CheckBox playSound;

    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());

    private Settings settings = Settings.INSTANCE;

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

        // Since Android likes to cut off long stack traces, if any Exceptions are thrown in the game we determine
        // their root cause and log it before propagating.
        try {
            ((GameController) loader.getController()).startGame();
        } catch (Exception e) {
            Throwable cause = null;
            Throwable result = e;

            while ((cause = result.getCause()) != null && result != cause) {
                result = cause;
            }

            Log.log(Level.SEVERE,  "Caught fatal exception:", result);
            throw e;
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playMusic.setSelected(settings.getBoolean("playMusic", true));
        playSound.setSelected(settings.getBoolean("playSound", true));
        for (Node checkBox : settingsRegion.getChildrenUnmodifiable()) {
            HBox.setHgrow(checkBox, Priority.ALWAYS);
        }

        logo.fitWidthProperty().bind(root.widthProperty());
    }

    @FXML
    private void setPlayMusic(ActionEvent event) {
        settings.putBoolean("playMusic", playMusic.isSelected());
    }

    @FXML
    private void setPlaySound(ActionEvent event) {
        settings.putBoolean("playSound", playSound.isSelected());
    }
}
