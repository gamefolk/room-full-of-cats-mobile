package org.gamefolk.roomfullofcats;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.gamefolk.roomfullofcats.utils.FXUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class SplashController implements Initializable {

    @FXML private Pane splashPane;

    private Transition mainMenuTransition;

    @FXML
    private void transitionToMainMenu(MouseEvent event) throws Exception {
        mainMenuTransition.stop();
        FXUtils.transitionScene(event, "/fxml/mainMenu.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FadeTransition ft = new FadeTransition(Duration.millis(2000), splashPane);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);

        mainMenuTransition = new SequentialTransition(ft, new PauseTransition(Duration.millis(2000)));
        mainMenuTransition.setOnFinished(
            (event) -> FXUtils.transitionScene((Stage) splashPane.getScene().getWindow(), "/fxml/mainMenu.fxml"));
        mainMenuTransition.play();
    }
}
