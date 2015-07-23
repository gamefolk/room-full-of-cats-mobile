package org.gamefolk.roomfullofcats;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import org.gamefolk.roomfullofcats.utils.FXUtils;

public class SplashController {

    @FXML
    private void transitionToMainMenu(MouseEvent event) throws Exception {
        FXUtils.transitionScene(event, "/fxml/mainMenu.fxml");
    }
}
