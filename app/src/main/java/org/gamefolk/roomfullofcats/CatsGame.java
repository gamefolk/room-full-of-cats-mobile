package org.gamefolk.roomfullofcats;

import com.eclipsesource.json.JsonObject;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class CatsGame {
    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());

    private long lastCatFall = 0;

    private Level currentLevel;
    private Bucket[] buckets;

    private Cat[][] map;
    private int score;

    private CatsGameLayout gameLayout;

    private MediaPlayer songPlayer;
    private AudioClip blipClip;
    private AudioClip scoreClip;

    public CatsGame(double width, double height) {
        this.gameLayout = new CatsGameLayout(width, height);

        loadResources();
    }

    private static Image loadImage(String path) {
        return new Image(RoomFullOfCatsApp.class.getResource(path).toString());
    }

    private static AudioClip loadAudioClip(String path) {
        return new AudioClip(RoomFullOfCatsApp.class.getResource(path).toString());
    }

    private static Media loadMedia(String path) {
        return new Media(RoomFullOfCatsApp.class.getResource(path).toString());
    }

    private static Rectangle2D getSpriteBounds(FrameAnimation sprite, int x, int y) {
        double xCoordinate = sprite.getWidth() * (x + 1);
        double yCoordinate = sprite.getHeight() * (y + 1);
        return new Rectangle2D(xCoordinate, yCoordinate, sprite.getWidth(), sprite.getHeight());
    }

    private void updateSprites() {
        long currentTime = System.currentTimeMillis();
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
                            score++;
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
            map[x][0] = new Cat(CatType.getRandomCat());
        }

        lastCatFall = currentTime;
    }

    public void makeLevel(Level level) {
        currentLevel = level;

        map = new Cat[level.mapWidth][level.mapHeight];
        buckets = new Bucket[level.mapWidth];
    }

    public Parent getLayout() {
        return this.gameLayout;
    }

    private void loadResources() {
        CatType.BLUE_CAT.loadFrames(
                "/img/bluecat1.png",
                "/img/bluecat2.png",
                "/img/bluecat3.png"
        );

        CatType.GRAY_CAT.loadFrames(
                "/img/graycat1.png",
                "/img/graycat2.png",
                "/img/graycat3.png"
        );

        CatType.PINK_CAT.loadFrames(
                "/img/pinkcat1.png",
                "/img/pinkcat2.png",
                "/img/pinkcat3.png"
        );

        CatType.STRIPE_CAT.loadFrames(
                "/img/stripecat1.png",
                "/img/stripecat2.png",
                "/img/stripecat3.png"
        );

        // TODO: Fix with https://bitbucket.org/javafxports/android/issue/47/app-crashes-with-media-api
        if (PlatformFeatures.MEDIA_SUPPORTED) {
            songPlayer = new MediaPlayer(loadMedia("/audio/catsphone.mp3"));
            blipClip = loadAudioClip("/audio/blip.wav");
            scoreClip = loadAudioClip("/audio/score.wav");
        }
    }

    public void drawSprites(GraphicsContext gc) {
        // Erase canvas
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // Draw falling cats
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == null) {
                    continue;
                }

                FrameAnimation sprite = map[i][j].sprite;
                Rectangle2D bounds = getSpriteBounds(sprite, i, j);
                gc.drawImage(sprite.getCurrentFrame(), bounds.getMinX(), bounds.getMinY());
            }
        }
    }

    private Level loadLevel(String path) {
        Level level = new Level();

        // TODO: Handle more level numbers
        level.number = 1;

        InputStream input = RoomFullOfCatsApp.class.getResourceAsStream(path);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try (Reader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (IOException e) {
            Log.severe("error reading level json");
            throw new RuntimeException(e);
        }
        Log.info("json: " + writer.toString());
        JsonObject mainObject = JsonObject.readFrom(writer.toString());
        Log.info("level: " + mainObject.get("levelTitle").asString());

        level.mapWidth = mainObject.get("columns").asInt();
        level.mapHeight = mainObject.get("rows").asInt();
        level.levelTime = mainObject.get("timeLimit").asInt();
        level.fallTime = 1;
        level.catsLimit = 3;
        level.message = mainObject.get("levelDescription").asString();
        level.title = mainObject.get("levelTitle").asString();

        return level;
    }

    public void startGame() {
        currentLevel = loadLevel("/levels/level1");
        makeLevel(currentLevel);

        final GraphicsContext gc = gameLayout.getGraphicsContext2D();

        // Create the game loop
        final Duration oneFrameDuration = Duration.millis(1000 / 60);   // 60 FPS
        final KeyFrame oneLoop = new KeyFrame(oneFrameDuration, actionEvent -> {
            updateSprites();

            drawSprites(gc);
        });
        Timeline gameLoop = new Timeline(oneLoop);
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();

        // Handle user input
        gc.getCanvas().setOnMouseClicked(mouseEvent -> {
            Log.info("Click at " + mouseEvent.getScreenX() + " " + mouseEvent.getScreenY());
            // Set any cats that intersect the event to null
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (map[i][j] == null) {
                        continue;
                    }

                    FrameAnimation sprite = map[i][j].sprite;
                    Rectangle2D bounds = getSpriteBounds(sprite, i, j);
                    Log.info("Cat " + i + ", " + j + ": " + bounds.toString());
                    if (bounds.contains(mouseEvent.getX(), mouseEvent.getY())) {
                        map[i][j] = null;
                        Log.info("setting cat " + i + " " + j + " to null");
                    }
                }
            }

        });

        // Play music
        // TODO: Fix with https://bitbucket.org/javafxports/android/issue/47/app-crashes-with-media-api
        if (PlatformFeatures.MEDIA_SUPPORTED) {
            songPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            songPlayer.play();
        }
    }

    private enum CatType {
        BLUE_CAT, GRAY_CAT, PINK_CAT, STRIPE_CAT;

        private static final Random RAND = new Random();

        private List<Image> frames;

        public static CatType getRandomCat() {
            return CatType.values()[RAND.nextInt(CatType.values().length)];
        }

        public void loadFrames(String... paths) {
            this.frames = new ArrayList<>();
            for (String path : paths) {
                frames.add(loadImage(path));
            }
        }

        public double getWidth() {
            return frames.get(0).getWidth();
        }

        public double getHeight() {
            return frames.get(0).getHeight();
        }
    }

    private class Level {
        int number;
        int mapWidth;
        int mapHeight;
        int levelTime;
        int fallTime;       // interval after which cats fall, in seconds
        int catsLimit;      // the target number of cats of the same type to collect
        String message;
        String title;
    }

    private class Bucket {
        public CatType type = null;
        public int things = 0;
        public FrameAnimation sprite;

        public Bucket(CatType type) {
            this.type = type;
            this.sprite = new FrameAnimation(null, Duration.millis(1000), type.getWidth(), type.getHeight(),
                    type.frames.toArray(new Image[type.frames.size()]));
        }
    }

    private class Cat {
        public CatType type;
        public FrameAnimation sprite;

        public Cat(CatType type) {
            this.type = type;
            this.sprite = new FrameAnimation(null, Duration.millis(1000), type.getWidth(), type.getHeight(),
                    type.frames.toArray(new Image[type.frames.size()]));
            this.sprite.setCycleCount(Animation.INDEFINITE);
            this.sprite.play();
        }

        @Override
        public String toString() {
            return this.type.name();
        }
    }
}
