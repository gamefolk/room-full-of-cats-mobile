package org.gamefolk.roomfullofcats;

import com.gluonhq.charm.down.common.PlatformFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class RoomFullOfCatsApp extends Application {
    private Stage stage;

    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());

    @Override
    public void init() throws Exception {
        Log.info("OS: " + System.getProperty("os.name"));
    }

	@Override
    public void start(final Stage primaryStage) throws Exception {
        Log.info("Platform name: " + PlatformFactory.getPlatform().getName());

        if (PlatformFeatures.ADS_SUPPORTED) {
            AdvertisingService advertisingService = AdvertisingService.getInstance();
            advertisingService.initializeAdService();
        }

        stage = primaryStage;

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/splash.fxml"));
        stage.setScene(new Scene(root, 360, 640, Color.BLACK));

        // Start full screen
        Log.info("Full screen support: " + PlatformFeatures.START_FULL_SCREEN);
        if (PlatformFeatures.START_FULL_SCREEN) {
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX(primaryScreenBounds.getMinX());
            stage.setY(primaryScreenBounds.getMinY());
            stage.setWidth(primaryScreenBounds.getWidth());
            stage.setHeight(primaryScreenBounds.getHeight());
        }

        stage.setResizable(false);

        stage.show();
    }
}
