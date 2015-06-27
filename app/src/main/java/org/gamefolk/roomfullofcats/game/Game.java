package org.gamefolk.roomfullofcats.game;

import com.eclipsesource.json.JsonObject;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.gamefolk.roomfullofcats.PlatformFeatures;
import org.gamefolk.roomfullofcats.RoomFullOfCatsApp;

import java.io.*;
import java.util.logging.Logger;

public class Game {
    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());

    private int score;
    private Level currentLevel;
    private Cat[][] map;
    private Bucket[] buckets;
    private long lastCatFall;
    private GraphicsContext gc;

    private Dimension2D catSize;
    private Point2D mapOrigin;

    private int canvasWidth;
    private int canvasHeight;

    private MediaPlayer songPlayer;
    private AudioClip blipClip;
    private AudioClip scoreClip;

    public Game(GraphicsContext gc) {
        this.gc = gc;

        this.canvasWidth = (int) gc.getCanvas().getWidth();
        this.canvasHeight = (int) gc.getCanvas().getHeight();

        Log.info("Canvas dimensions are " + new Dimension2D(canvasWidth, canvasHeight));

        // TODO: Fix with https://bitbucket.org/javafxports/android/issue/47/app-crashes-with-media-api
        if (PlatformFeatures.MEDIA_SUPPORTED) {
            songPlayer = new MediaPlayer(loadMedia("/assets/audio/catsphone.mp3"));
            blipClip = loadAudioClip("/assets/audio/blip.wav");
            scoreClip = loadAudioClip("/assets/audio/score.wav");
        }
    }

    private static AudioClip loadAudioClip(String path) {
        return new AudioClip(RoomFullOfCatsApp.class.getResource(path).toString());
    }

    private static Media loadMedia(String path) {
        return new Media(RoomFullOfCatsApp.class.getResource(path).toString());
    }

    private static Level loadLevel(String path) {
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

        String message = mainObject.get("levelDescription").asString();
        String title = mainObject.get("levelTitle").asString();

        // TODO: Handle more level numbers
        return new Level.Builder(1, message, title)
                .mapWidth(mainObject.get("columns").asInt())
                .mapHeight(mainObject.get("rows").asInt())
                .levelTime(mainObject.get("timeLimit").asInt())
                .build();
    }

    private Rectangle2D getCatBounds(int x, int y) {
        double xCoordinate = mapOrigin.getX() + catSize.getWidth() * x;
        double yCoordinate = mapOrigin.getY() + catSize.getHeight() * y;
        return new Rectangle2D(xCoordinate, yCoordinate, catSize.getWidth(), catSize.getHeight());
    }

    public void setLevel(String path) {
        currentLevel = loadLevel(path);
        map = new Cat[currentLevel.mapWidth][currentLevel.mapHeight];
        buckets = new Bucket[currentLevel.mapWidth];

        // Calculate the width of cats, leaving a cat length on either side for margin.
        int catWidth;
        if (currentLevel.mapWidth % 2 == 0) {
            catWidth = canvasWidth / (currentLevel.mapWidth + 2);
            mapOrigin = new Point2D(catWidth, catWidth);
        } else {
            // If there are an odd number of cats, then the middle row of cats, then we do the calculation as if there is one more cat, leaving some extra space on both sides.
            catWidth = canvasWidth / ((currentLevel.mapWidth + 1) + 2);
            mapOrigin = new Point2D(catWidth + catWidth / 2, catWidth);
        }
        catSize = new Dimension2D(catWidth, catWidth);
        mapOrigin = new Point2D(catWidth, catWidth);

        Log.info("Cat size set to " + catSize);
        Log.info("Map origin set to " + mapOrigin);

        // TODO: Fix with https://bitbucket.org/javafxports/android/issue/47/app-crashes-with-media-api
        if (PlatformFeatures.MEDIA_SUPPORTED && !songPlayer.isAutoPlay()) {
            songPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            songPlayer.play();
        }
    }

    public void updateSprites() {
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
                            // TODO: Fix when Media is supported on all platforms
                            if (PlatformFeatures.MEDIA_SUPPORTED && !scoreClip.isPlaying()) {
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
