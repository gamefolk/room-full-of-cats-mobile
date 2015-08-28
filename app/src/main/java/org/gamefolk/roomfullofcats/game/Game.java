package org.gamefolk.roomfullofcats.game;

import javafx.beans.property.*;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

import org.gamefolk.roomfullofcats.MusicPlayer;
import org.gamefolk.roomfullofcats.RoomFullOfCatsApp;
import org.gamefolk.roomfullofcats.Sound;
import org.gamefolk.roomfullofcats.SoundService;
import org.gamefolk.roomfullofcats.game.goals.Goal;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Map;
import java.util.logging.Logger;

public class Game {
    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());
    private final GraphicsContext gc;
    private final MusicPlayer songPlayer;
    private final Sound blipClip;
    private final Sound scoreClip;
    private IntegerProperty score = new SimpleIntegerProperty(0);
    private ObjectProperty<Duration> timeRemaining = new SimpleObjectProperty<>();
    private StringProperty goal = new SimpleStringProperty();
    private Interval gameTime;
    private Level currentLevel;
    private Cat[][] map;
    private Bucket[] buckets;
    private long lastCatFall;
    private Dimension2D catSize;
    private Point2D mapOrigin;
    private int canvasWidth;
    private int canvasHeight;
    private PeriodFormatter timerFormat;
    private boolean gameOver;
    private CountdownTimer timer;

    public Game(GraphicsContext gc) {
        this.gc = gc;

        SoundService soundService = SoundService.getInstance();
        songPlayer = soundService.loadMusic("/assets/audio/catsphone.mp3");
        blipClip = soundService.loadSound("/assets/audio/blip.wav");
        scoreClip = soundService.loadSound("/assets/audio/score.wav");

        gameOver = false;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGoalsSatisfied() {
        for (Goal goal : currentLevel.goals) {
            if (!goal.isSatisfied(this)) {
                return false;
            }
        }

        return true;
    }

    public void startTimer() {
        timer.start();
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    public ObjectProperty<Duration> timeRemainingProperty() {
        return timeRemaining;
    }

    public StringProperty goalProperty() {
        return goal;
    }

    private Rectangle2D getCatBounds(int x, int y) {
        double xCoordinate = mapOrigin.getX() + catSize.getWidth() * x;
        double yCoordinate = mapOrigin.getY() + catSize.getHeight() * y;
        return new Rectangle2D(xCoordinate, yCoordinate, catSize.getWidth(), catSize.getHeight());
    }

    public void setLevel(Level level) {
        currentLevel = level;

        timer = new CountdownTimer.Builder(currentLevel.timeLimit).build();
        timeRemaining = new SimpleObjectProperty<>(currentLevel.timeLimit);

        goal.set(getGoalDescription());
    }
    
    public void layoutLevel() {
    	this.canvasWidth = (int) gc.getCanvas().getWidth();
        this.canvasHeight = (int) gc.getCanvas().getHeight();

        Log.info("Canvas dimensions are " + new Dimension2D(canvasWidth, canvasHeight));
        
    	int columns = (int) currentLevel.dimensions.width;
        int rows = (int) currentLevel.dimensions.height;
        map = new Cat[columns][rows];
        buckets = new Bucket[columns];

        // Calculate the size of cats, leaving a cat length on either side for margin.
        
        int tempX = canvasWidth / (columns + 1); 
        int tempY = canvasHeight / (rows + 1);
        
        int catXY = tempX < tempY ? tempX : tempY;
        
        catSize = new Dimension2D(catXY, catXY);
        mapOrigin = new Point2D((canvasWidth - (columns * catXY)) / 2, (canvasHeight - (rows * catXY)) / 2);

        Log.info("Cat size set to " + catSize);
        Log.info("Map origin set to " + mapOrigin);
    }

    private String getGoalDescription() {
        PeriodFormatter levelTimeFormatter = new PeriodFormatterBuilder()
                .appendMinutes()
                .appendSuffix(" minute", " minutes")
                .appendSeparator(" and ")
                .appendSeconds()
                .appendSuffix(" second", " seconds")
                .toFormatter();

        StringBuilder goalDescription = new StringBuilder();

        // Required score will always be the first goal.
        goalDescription.append(currentLevel.goals.get(0).getDescription());
        goalDescription.append('\n');

        goalDescription.append(String.format("You have %s.\n", levelTimeFormatter.print(currentLevel.timeLimit.toPeriod())));
        if (currentLevel.fallTime.getMillis() < Level.DEFAULT_FALL_TIME) {
            goalDescription.append("Cats will fall faster than normal.\n");
        } else if (currentLevel.fallTime.getMillis() > Level.DEFAULT_FALL_TIME) {
            goalDescription.append("Cats will fall slower than normal.\n");
        }

        if (currentLevel.catsLimit != Level.DEFAULT_CATS_LIMIT) {
            goalDescription.append(String.format("%d cats fit in a basket.\n", currentLevel.catsLimit));
        }

        for (Goal goal : currentLevel.goals.subList(1, currentLevel.goals.size())) {
            goalDescription.append(goal.getDescription());
            goalDescription.append('\n');
        }

        return goalDescription.toString();
    }

    public void playMusic() {
        songPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        songPlayer.play();
    }

    public int getNumMoves() {
        throw new UnsupportedOperationException("unimplemented");
    }

    public Map<Cat.Type, Integer> getNumMatches() {
        throw new UnsupportedOperationException("unimplemented");
    }

    public void updateSprites() {
        long currentTime = System.currentTimeMillis();
        if (timer.getRemainingTime() <= 0) {
            gameOver = true;
            return;
        }

        timeRemaining.set(Duration.millis(timer.getRemainingTime()));

        // Only make the cats fall when we need to.
        if (currentTime - lastCatFall < 2000) {
            return;
        }

        // move bottom row into buckets
        for (int x = 0; x < map.length; x++) {
            Cat candidate = map[x][map[x].length - 1];

            if (candidate != null) {
                Bucket current = buckets[x];

                if (current != null) {
                    if (candidate.type == current.type) {
                        current.things++;

                        if (current.things == currentLevel.catsLimit) {
                            score.set(score.get() + 1);
                            if (!scoreClip.isPlaying()) {
                                scoreClip.play();
                            }
                            buckets[x] = null;
                        }
                    } else {
                        Bucket newBucket = new Bucket(candidate.type);
                        buckets[x] = newBucket;
                    }
                } else {
                    Bucket newBucket = new Bucket(candidate.type);
                    buckets[x] = newBucket;
                }
            }
        }

        // descend cats in all other rows
        for (int y = map[0].length - 1; y > 0; y--) {
            for (int x = 0; x < map.length; x++) {
                map[x][y] = map[x][y - 1];
            }
        }
        // fill the top row with new cats
        for (int x = 0; x < map.length; x++) {
            map[x][0] = new Cat(Cat.Type.getRandomCat());
        }

        lastCatFall = currentTime;
    }

    public void pause() {
        songPlayer.pause();
        timer.stop();
    }

    public void unpause() {
        songPlayer.play();
        timer.start();
    }

    public void drawSprites() {
        // Erase canvas
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // Draw falling cats
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == null) {
                    continue;
                }

                FrameAnimation sprite = map[i][j].sprite;
                Rectangle2D bounds = getCatBounds(i, j);
                gc.drawImage(
                        sprite.getCurrentFrame(),
                        bounds.getMinX(),
                        bounds.getMinY(),
                        bounds.getWidth(),
                        bounds.getHeight());
            }
        }

        for (int i = 0; i < buckets.length; i++) {
            Bucket bucket = buckets[i];

            if (bucket == null) {
                continue;
            }

            FrameAnimation sprite = bucket.sprite;
            Rectangle2D bounds = getCatBounds(i, (int) currentLevel.dimensions.height);
            gc.drawImage(
                    sprite.getCurrentFrame(),
                    bounds.getMinX(),
                    bounds.getMinY(),
                    bounds.getWidth() + (bucket.things * 2),
                    bounds.getHeight() + (bucket.things * 2));
        }
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public void stopMusic() {
        songPlayer.stop();
    }

    public void removeCat(double x, double y) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == null) {
                    continue;
                }

                Rectangle2D bounds = getCatBounds(i, j);
                if (bounds.contains(x, y)) {
                    map[i][j] = null;
                }
            }
        }
    }
}
