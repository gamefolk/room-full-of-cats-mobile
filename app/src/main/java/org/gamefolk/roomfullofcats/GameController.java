package org.gamefolk.roomfullofcats;

import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import org.gamefolk.roomfullofcats.game.Cat;
import org.gamefolk.roomfullofcats.game.Game;
import org.gamefolk.roomfullofcats.game.Level;
import org.gamefolk.roomfullofcats.utils.FXUtils;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class GameController implements Initializable {
    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());
    private Game game;

    @FXML private BorderPane root;
    @FXML private BorderPane scoreView;
    @FXML private Pane gameView;
    @FXML private Canvas canvas;
    @FXML private Text title;
    @FXML private Text message;
    @FXML private Text score;
    @FXML private Text time;
    @FXML private Text goal;
    @FXML private Parent gameOverView;
    @FXML private ToggleButton pauseButton;
    @FXML private BorderPane ad;

    private Timeline gameLoop;

    public void startGame(Level level) {
        Log.info("Starting game.");

        // Pause the game if we lose focus
        root.getScene().getWindow().focusedProperty().addListener((ov, t, t1) -> {
            if (ov.getValue()) {
                unpause();
            } else {
                pause();
            }
        });

        // Reset everything
        root.applyCss();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        title.getParent().setOpacity(1.0);
        gameOverView.setVisible(false);

        gameView.setFocusTraversable(true);

        game = new Game(canvas.getGraphicsContext2D());
        game.setLevel(level);
        game.playMusic();

        // Create the game loop
        final Duration oneFrameDuration = Duration.millis(1000 / 60);   // 60 FPS
        final KeyFrame oneLoop = new KeyFrame(oneFrameDuration, actionEvent -> {
            game.updateSprites();

            if (game.isGameOver()) {
                gameOver();
            }

            game.drawSprites();
        });

        gameLoop = new Timeline(oneLoop);
        gameLoop.setCycleCount(Timeline.INDEFINITE);

        Bindings.bindBidirectional(score.textProperty(), game.scoreProperty(), new NumberStringConverter());
        time.textProperty().bindBidirectional(game.timeRemainingProperty(), new StringConverter<org.joda.time.Duration>() {
            private PeriodFormatter timerFormat;

            {
                timerFormat = new PeriodFormatterBuilder()
                    .printZeroAlways()
                    .minimumPrintedDigits(1)
                    .appendMinutes()
                    .appendSeparator(":")
                    .minimumPrintedDigits(2)
                    .appendSeconds()
                    .toFormatter();
            }

            @Override
            public String toString(org.joda.time.Duration duration) {
                return timerFormat.print(duration.toPeriod());
            }

            @Override
            public org.joda.time.Duration fromString(String s) {
                // We don't really have a way to convert back (nor do we need to), so let's just return a default.
                return org.joda.time.Duration.millis(0);
            }
        });
        Bindings.bindBidirectional(goal.textProperty(), game.goalProperty());

        message.setWrappingWidth(root.getWidth() * 0.75);

        // Fade the level title and description in typewriter-style.
        Animation typewriterAnimation = new Transition() {
            {
                setCycleDuration(Duration.millis(2000));
            }

            /**
             * Calculates the length of the string in the "typewriter" animation.
             */
            private int calculateLength(String str, double frac) {
                return Math.round(str.length() * (float) frac);
            }

            @Override
            protected void interpolate(double frac) {
                title.setText(level.title.substring(0, calculateLength(level.title, frac)));
                message.setText(level.description.substring(0, calculateLength(level.description, frac)));
            }
        };

        // Animation to display the level title and description, then a brief pause.
        SequentialTransition introAnimation = new SequentialTransition(
            typewriterAnimation,
            new PauseTransition(Duration.millis(2000))
        );

        // Then, we fade the text as the game starts playing.
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), title.getParent());
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0);

        introAnimation.setOnFinished((event) -> {        	
        	
        	// Make sure the canvas is the correct width.
            canvas.widthProperty().set(root.getWidth());
            canvas.heightProperty().set(root.getHeight() - (scoreView.getHeight() + ad.getHeight()));
            
            game.layoutLevel();
        	
            fadeTransition.play();

            // Play the game!
            game.startTimer();
            gameLoop.play();
            pauseButton.setDisable(false);
        });
        introAnimation.play();
    }

    @FXML void handleInput(MouseEvent event) {
        if (game.isGameOver() || pauseButton.isSelected()) {
            return;
        }

        game.removeCat(event.getX(), event.getY());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadResources();
    }

    private void gameOver() {
        gameOverView.setVisible(true);
        pauseButton.setDisable(true);
        if (game.isGoalsSatisfied()) {
            goal.setFill(Color.GREEN);
            if (game.getCurrentLevel().getStatus() != Level.Status.WON) {
                game.getCurrentLevel().setStatus(Level.Status.WON);
            }
        } else {
            goal.setFill(Color.RED);
            Level.Status currentStatus = game.getCurrentLevel().getStatus();
            if (currentStatus != Level.Status.LOST && currentStatus != Level.Status.WON) {
                game.getCurrentLevel().setStatus(Level.Status.LOST);
            }
        }
    }

    private void pause() {
        game.pause();
        gameLoop.pause();
    }

    private void unpause() {
        game.unpause();
        gameLoop.play();
    }

    @FXML
    private void pauseButtonToggle() {
        if (pauseButton.isSelected()) {
            pause();
        } else {
            unpause();
        }
    }

    @FXML
    private void transitionToMainMenu(ActionEvent event) {
        game.stopMusic();
        gameLoop.stop();
        FXUtils.transitionScene(event, "/fxml/mainMenu.fxml");
    }

    @FXML
    private void restart() {
        game.stopMusic();
        gameLoop.stop();
        startGame(game.getCurrentLevel());
    }

    private void loadResources() {
        Cat.Type.BLUE_CAT.loadFrames(
                "/assets/img/bluecat1.png",
                "/assets/img/bluecat2.png",
                "/assets/img/bluecat3.png"
        );

        Cat.Type.GRAY_CAT.loadFrames(
                "/assets/img/graycat1.png",
                "/assets/img/graycat2.png",
                "/assets/img/graycat3.png"
        );

        Cat.Type.PINK_CAT.loadFrames(
                "/assets/img/pinkcat1.png",
                "/assets/img/pinkcat2.png",
                "/assets/img/pinkcat3.png"
        );

        Cat.Type.STRIPE_CAT.loadFrames(
                "/assets/img/stripecat1.png",
                "/assets/img/stripecat2.png",
                "/assets/img/stripecat3.png"
        );
    }
}
