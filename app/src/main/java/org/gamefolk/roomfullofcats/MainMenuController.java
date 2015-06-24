package org.gamefolk.roomfullofcats;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {
    @FXML private HBox settings;
    @FXML private BorderPane root;
    @FXML private ImageView logo;

    @FXML
    private void startGame(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        CatsGame game = new CatsGame(stage.getWidth(), stage.getHeight());
        stage.getScene().setRoot(game.getLayout());
        game.startGame();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (Node checkBox : settings.getChildrenUnmodifiable()) {
            HBox.setHgrow(checkBox, Priority.ALWAYS);
        }

        logo.fitWidthProperty().bind(root.widthProperty());
    }
}
