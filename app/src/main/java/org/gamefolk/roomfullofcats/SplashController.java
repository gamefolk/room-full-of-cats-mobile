package org.gamefolk.roomfullofcats;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SplashController {

    @FXML
    private void transitionToMainMenu(MouseEvent event) throws Exception {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Parent mainMenu = FXMLLoader.load(getClass().getResource("/fxml/mainMenu.fxml"));
        stage.getScene().setRoot(mainMenu);
    }
}
