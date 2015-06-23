package org.gamefolk.roomfullofcats;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class RoomFullOfCatsApp extends Application {
    private Stage stage;

	private CatsGame game;
    private SplashLayout splashLayout;
    private MainMenuLayout mainMenuLayout;
	
    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());

    @Override
    public void init() throws Exception {
        Log.info("OS: " + System.getProperty("os.name"));

        splashLayout = new SplashLayout(event -> stage.getScene().setRoot(mainMenuLayout));
        mainMenuLayout = new MainMenuLayout(actionEvent -> startGame(), null);
    }

	@Override
    public void start(final Stage primaryStage) throws Exception {
        stage = primaryStage;

        stage.setScene(new Scene(splashLayout, 1024, 768, Color.BLACK));

        // Start full screen
        Log.info("Full screen support: " + PlatformFeatures.START_FULL_SCREEN);
        if (PlatformFeatures.START_FULL_SCREEN) {
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX(primaryScreenBounds.getMinX());
            stage.setY(primaryScreenBounds.getMinY());
            stage.setWidth(primaryScreenBounds.getWidth());
            stage.setHeight(primaryScreenBounds.getHeight());
        }

        stage.show();
    }

    private void startGame() {
		Log.info("finished loading");

		game = new CatsGame(stage.getWidth(), stage.getHeight());

        stage.setScene(new Scene(game.getLayout(), stage.getWidth(), stage.getHeight()));
        game.startGame();
	}
}
