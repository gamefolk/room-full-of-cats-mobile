package org.gamefolk.roomfullofcats.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.media.MediaPlayer;
import org.gamefolk.roomfullofcats.*;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

public class Game {
    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());
    private final GraphicsContext gc;
    private final MusicPlayer songPlayer;
    private final Sound blipClip;
    private final Sound scoreClip;
    private final Settings settings = Settings.INSTANCE;
    private IntegerProperty score = new SimpleIntegerProperty(0);
    private StringProperty timer = new SimpleStringProperty();
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

    public Game(GraphicsContext gc) {
        this.gc = gc;

        this.canvasWidth = (int) gc.getCanvas().getWidth();
        this.canvasHeight = (int) gc.getCanvas().getHeight();

        Log.info("Canvas dimensions are " + new Dimension2D(canvasWidth, canvasHeight));

        SoundService soundService = SoundService.getInstance();
        songPlayer = soundService.loadMusic("/assets/audio/catsphone.mp3");
        blipClip = soundService.loadSound("/assets/audio/blip.wav");
        scoreClip = soundService.loadSound("/assets/audio/score.wav");

        timerFormat = new PeriodFormatterBuilder()
                .printZeroAlways()
                .minimumPrintedDigits(1)
                .appendMinutes()
                .appendSeparator(":")
                .minimumPrintedDigits(2)
                .appendSeconds()
                .toFormatter();

    }

    private String formatTimer(Duration duration) {
        return timerFormat.print(duration.toPeriod());
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    public StringProperty timerProperty() {
        return timer;
    }

    private Rectangle2D getCatBounds(int x, int y) {
        double xCoordinate = mapOrigin.getX() + catSize.getWidth() * x;
        double yCoordinate = mapOrigin.getY() + catSize.getHeight() * y;
        return new Rectangle2D(xCoordinate, yCoordinate, catSize.getWidth(), catSize.getHeight());
    }

    public void setLevel(String path) {
        try {
            currentLevel = Level.loadLevel(path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        map = new Cat[currentLevel.mapWidth][currentLevel.mapHeight];
        buckets = new Bucket[currentLevel.mapWidth];

        // Calculate the width of cats, leaving a cat length on either side for margin.
        int catWidth, catHeight;
        if (currentLevel.mapWidth % 2 == 0) {
            catWidth = canvasWidth / (currentLevel.mapWidth + 2);
            catHeight = catWidth;
            mapOrigin = new Point2D(catWidth, catHeight);
        } else {
            // If there are an odd number of cats, then the middle row of cats, then we do the calculation as if there is one more cat, leaving some extra space on both sides.
            catWidth = canvasWidth / ((currentLevel.mapWidth + 1) + 2);
            catHeight = catWidth;
            mapOrigin = new Point2D(catWidth + catWidth / 2, catHeight);
        }
        catSize = new Dimension2D(catWidth, catHeight);
        mapOrigin = new Point2D(catWidth, catHeight);

        Log.info("Cat size set to " + catSize);
        Log.info("Map origin set to " + mapOrigin);

        if (settings.getBoolean("playMusic", true)) {
            songPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            songPlayer.play();
        }

        gameTime = currentLevel.levelTime.toIntervalFrom(Instant.now());
        timer.set(formatTimer(gameTime.toDuration()));
    }

    public void updateSprites() {
        long currentTime = System.currentTimeMillis();
        Duration timeLeft = new Duration(currentTime, gameTime.getEndMillis());
        timer.set(formatTimer(timeLeft));

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
                            if (settings.getBoolean("playSound", true) && !scoreClip.isPlaying()) {
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
            map[x][0] = new Cat(CatType.getRandomCat());
        }

        lastCatFall = currentTime;
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
