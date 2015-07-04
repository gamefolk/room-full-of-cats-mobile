package org.gamefolk.roomfullofcats;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import org.gamefolk.roomfullofcats.game.CatType;
import org.gamefolk.roomfullofcats.game.Game;
import org.joda.time.Interval;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class GameController implements Initializable {
    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());
    private Game game;

    @FXML private BorderPane root;
    @FXML private Pane gameView;
    @FXML private Canvas canvas;
    @FXML private Text score;
    @FXML private Text time;

    public GraphicsContext getGraphicsContext2D() {
        return canvas.getGraphicsContext2D();
    }

    public void startGame() {
        Log.info("Starting game.");

        game = new Game(getGraphicsContext2D());
        game.setLevel("/assets/levels/level1");

        // Create the game loop
        final Duration oneFrameDuration = Duration.millis(1000 / 60);   // 60 FPS
        final KeyFrame oneLoop = new KeyFrame(oneFrameDuration, actionEvent -> {
            game.updateSprites();

            game.drawSprites();
        });
        Timeline gameLoop = new Timeline(oneLoop);
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();

        Bindings.bindBidirectional(score.textProperty(), game.scoreProperty(), new NumberStringConverter());
        PeriodFormatter fmt = new PeriodFormatterBuilder()
                .appendMinutes()
                .appendSeparator(":")
                .appendSeconds()
                .toFormatter();
        Bindings.bindBidirectional(time.textProperty(), game.gameLengthProperty(), new StringConverter<Interval>() {
            @Override
            public String toString(Interval interval) {
                return fmt.print(interval.toPeriod());
            }

            @Override
            public Interval fromString(String s) {
                return new Interval(s);
            }
        });
    }

    @FXML void handleInput(MouseEvent event) {
        game.removeCat(event.getX(), event.getY());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadResources();

        canvas.heightProperty().bind(gameView.heightProperty());
        canvas.widthProperty().bind(gameView.widthProperty());
    }

    private void loadResources() {
        CatType.BLUE_CAT.loadFrames(
                "/assets/img/bluecat1.png",
                "/assets/img/bluecat2.png",
                "/assets/img/bluecat3.png"
        );

        CatType.GRAY_CAT.loadFrames(
                "/assets/img/graycat1.png",
                "/assets/img/graycat2.png",
                "/assets/img/graycat3.png"
        );

        CatType.PINK_CAT.loadFrames(
                "/assets/img/pinkcat1.png",
                "/assets/img/pinkcat2.png",
                "/assets/img/pinkcat3.png"
        );

        CatType.STRIPE_CAT.loadFrames(
                "/assets/img/stripecat1.png",
                "/assets/img/stripecat2.png",
                "/assets/img/stripecat3.png"
        );
    }
}
