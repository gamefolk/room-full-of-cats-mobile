package org.gamefolk.roomfullofcats;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.gamefolk.roomfullofcats.utils.FXUtils;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class MainMenuController implements Initializable {
    @FXML private HBox settingsRegion;
    @FXML private BorderPane root;
    @FXML private ImageView logo;
    @FXML private CheckBox playMusic;
    @FXML private CheckBox playSound;

    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());

    private final Settings settings = Settings.INSTANCE;

    @FXML
    private void startGame(ActionEvent event) {
        FXUtils.transitionScene(event, "/fxml/levelSelect.fxml");
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
