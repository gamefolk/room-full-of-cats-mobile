package org.gamefolk.roomfullofcats;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

import org.gamefolk.roomfullofcats.utils.FXUtils;

public class SplashController {
	
	@FXML private Pane splashPane;
	
    public void startSplash() {
    	
    	Stage stage = (Stage) splashPane.getScene().getWindow();
    	Rectangle rect = new Rectangle(stage.getWidth(), stage.getHeight(), Color.BLACK);
    	splashPane.getChildren().add(rect);
    	
    	FadeTransition ft = new FadeTransition(Duration.millis(2000), rect);
    	ft.setFromValue(1.0);
    	ft.setToValue(0.0);
    	
    	SequentialTransition st = new SequentialTransition(
    			ft,
                new PauseTransition(Duration.millis(2000))
            );
    	
    	st.setOnFinished((event) -> {
    		FXMLLoader fxmlLoader = new FXMLLoader(FXUtils.class.getResource("/fxml/mainMenu.fxml"));

            Parent newParent;
            try {
                newParent = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.getScene().setRoot(newParent);
    	});
    	
    	st.play();
    }
	
    @FXML
    private void transitionToMainMenu(MouseEvent event) throws Exception {
        FXUtils.transitionScene(event, "/fxml/mainMenu.fxml");
    }
}
