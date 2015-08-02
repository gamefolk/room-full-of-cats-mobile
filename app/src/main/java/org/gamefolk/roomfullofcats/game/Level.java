package org.gamefolk.roomfullofcats.game;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.sun.javafx.geom.Dimension2D;
import org.gamefolk.roomfullofcats.RoomFullOfCatsApp;
import org.gamefolk.roomfullofcats.utils.JsonUtils;
import org.joda.time.Duration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A class that represents in-game levels, as defined by several parameters in a JSON object.
 *
 * levelTitle (required)        The title for the individual level for display on level select screens, etc.
 * levelDescription (required)  The descriptive flavor text for the level that describes its goal or mechanics
 * rows (required)              The number of rows for the playfield
 * columns (required)           The number of colums for the playfield
 * timeLimit (required)         Time limit for the level in seconds
 * requiredScore (required)     The minimum score the player is required to achieve in order to win the level
 * fallTime (default 1000ms)    Starting speed at which the cats fall, in milliseconds
 * catsLimit (default 4)        The target number of cats of the same type to collect
 * moveLimit (default 0)        The number of cats the player is allowed to remove before failing the level
 * requiredMatch (optional)     An object mapping the number of cats of a given type that are required.
 *      blue (default 0)         The number of full baskets of blue cats the player must achieve to win the level
 *      gray (default 0)         The number of full baskets of gray cats the player must achieve to win the level
 *      pink (default 0)         The number of full baskets of pink cats the player must achieve to win the level
 *      stripe (default 0)       The number of full baskets of stripe cats the player must achieve to win the level
 * glitchTypeSpawn (optional)   Used only for levels of type "glitch", specifies which of the four types of glitch cat
 *                              will spawn in the level. Currently unimplemented.
 */
public class Level {
    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());

    public static final int DEFAULT_FALL_TIME = 1000;
    public static final int DEFAULT_CATS_LIMIT = 4;
    public static final int DEFAULT_MOVE_LIMIT = 0;

    public final int number;

    public final String title;
    public final String description;
    public Dimension2D dimensions;
    public final Duration timeLimit;
    public final int requiredScore;
    public final Duration fallTime;
    public final int catsLimit;
    public final int moveLimit;
    public final Map<CatType, Integer> requiredMatches;

    private Level(int number, String title, String description, Dimension2D dimensions, Duration timeLimit,
                  int requiredScore, Duration fallTime, int catsLimit, int moveLimit,
                  Map<CatType, Integer> requiredMatches) {
        this.number = number;
        this.title = title;
        this.description = description;
        this.timeLimit = timeLimit;
        this.fallTime = fallTime;
        this.catsLimit = catsLimit;
        this.moveLimit = moveLimit;
        this.dimensions = dimensions;
        this.requiredScore = requiredScore;
        this.requiredMatches = requiredMatches;
    }

    public static Level loadLevel(String path, int number) throws FileNotFoundException {
        JsonObject levelObject;
        try {
            levelObject = JsonUtils.readJsonResource(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String title = levelObject.get("levelTitle").asString();
        String description = levelObject.get("levelDescription").asString();
        Builder levelBuilder = new Builder(number, title, description);

        int width = levelObject.get("columns").asInt();
        int height = levelObject.get("rows").asInt();
        levelBuilder = levelBuilder.mapDimensions(width, height);

        Duration levelTime = Duration.standardSeconds(levelObject.get("timeLimit").asLong());
        levelBuilder = levelBuilder.levelTime(levelTime);

        int score = levelObject.get("requiredScore").asInt();
        levelBuilder = levelBuilder.requiredScore(score);

        Duration fallTime = Duration.millis(levelObject.getLong("fallTime", DEFAULT_FALL_TIME));
        levelBuilder = levelBuilder.fallTime(fallTime);

        int catsLimit = levelObject.getInt("catsLimit", DEFAULT_CATS_LIMIT);
        levelBuilder = levelBuilder.catsLimit(catsLimit);

        int moveLimit = levelObject.getInt("moveLimit", DEFAULT_MOVE_LIMIT);
        levelBuilder = levelBuilder.moveLimit(moveLimit);

        JsonValue matchObject = levelObject.get("requiredMatch");
        Map<CatType, Integer> matches = new HashMap<>();
        if (matchObject != null) {
            JsonObject requiredMatches = matchObject.asObject();
            matches.put(CatType.BLUE_CAT, requiredMatches.getInt("blue", 0));
            matches.put(CatType.GRAY_CAT, requiredMatches.getInt("gray", 0));
            matches.put(CatType.PINK_CAT, requiredMatches.getInt("pink", 0));
            matches.put(CatType.STRIPE_CAT, requiredMatches.getInt("stripe", 0));
        }
        levelBuilder = levelBuilder.requiredMatches(matches);

        Log.info("level: " + title);

        return levelBuilder.build();
    }

    private static class Builder {
        private final int number;
        private final String title;
        private final String description;
        private Dimension2D mapDimensions;
        private Duration levelTime;
        private int requiredScore;
        private Duration fallTime;
        private int catsLimit;
        private int moveLimit;
        private Map<CatType, Integer> requiredMatches;

        public Builder(int number, String title, String description) {
            this.number = number;
            this.description = description;
            this.title = title;
            this.requiredMatches = new HashMap<>();
        }

        public Builder mapDimensions(int width, int height) {
            this.mapDimensions = new Dimension2D(width, height);
            return this;
        }

        public Builder levelTime(Duration levelTime) {
            this.levelTime = levelTime;
            return this;
        }

        public Builder requiredScore(int score) {
            this.requiredScore = score;
            return this;
        }

        public Builder fallTime(Duration fallTime) {
            this.fallTime = fallTime;
            return this;
        }

        public Builder catsLimit(int catsLimit) {
            this.catsLimit = catsLimit;
            return this;
        }

        public Builder requiredMatches(Map<CatType, Integer> matches) {
            this.requiredMatches = matches;
            return this;
        }

        public Builder moveLimit(int moveLimit) {
            this.moveLimit = moveLimit;
            return this;
        }

        public Level build() {
            return new Level(number, title, description, mapDimensions, levelTime, requiredScore, fallTime, catsLimit,
                             moveLimit, requiredMatches);
        }
    }
}
