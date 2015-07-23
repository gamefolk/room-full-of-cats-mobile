package org.gamefolk.roomfullofcats;

import com.gluonhq.charm.down.common.PlatformFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoomFullOfCatsApp extends Application {

    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());

    private enum Device {
        IPhone5(320, 568),
        IPhone6(375, 667);

        public static final Device DEFAULT = IPhone6;

        private Dimension2D screenSize;

        Device(int width, int height) {
            screenSize = new Dimension2D(width, height);
        }

        public Dimension2D getScreenSize() {
            return screenSize;
        }
    }

    @Override
    public void init() throws Exception {
        Log.info("OS: " + System.getProperty("os.name"));
    }

    public Dimension2D getRequestedScreenSize() {
        String screenSizeParam = getParameters().getNamed().get("screen-size");
        if (screenSizeParam == null) {
            // No screen size specified; return a default value.
            return Device.DEFAULT.getScreenSize();
        }

        for (Device device : Device.values()) {
            if (screenSizeParam.equalsIgnoreCase(device.name())) {
                return device.getScreenSize();
            }
        }

        // Match a simple regex for the dimensions.
        Pattern r = Pattern.compile("(\\d+)x(\\d+)");
        Matcher match = r.matcher(screenSizeParam);
        if (match.find()) {
            return new Dimension2D(Integer.parseInt(match.group(0)), Integer.parseInt(match.group(1)));
        }

        return Device.DEFAULT.getScreenSize();
    }

	@Override
    public void start(final Stage primaryStage) throws Exception {
        Log.info("Command line args: " + getParameters().getRaw());
        Log.info("Platform name: " + PlatformFactory.getPlatform().getName());

        if (PlatformFeatures.ADS_SUPPORTED) {
            AdvertisingService advertisingService = AdvertisingService.getInstance();
            advertisingService.initializeAdService();
        }

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/splash.fxml"));
        Dimension2D screenSize = getRequestedScreenSize();
        Log.info("Setting screen size to " + screenSize);
        primaryStage.setScene(new Scene(root, screenSize.getWidth(), screenSize.getHeight(), Color.BLACK));

        // Start full screen
        Log.info("Full screen support: " + PlatformFeatures.START_FULL_SCREEN);
        if (PlatformFeatures.START_FULL_SCREEN) {
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setX(primaryScreenBounds.getMinX());
            primaryStage.setY(primaryScreenBounds.getMinY());
            primaryStage.setWidth(primaryScreenBounds.getWidth());
            primaryStage.setHeight(primaryScreenBounds.getHeight());
        }

        primaryStage.setResizable(false);

        primaryStage.show();
    }
}
