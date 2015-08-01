package org.gamefolk.roomfullofcats.game;

import com.eclipsesource.json.JsonObject;
import org.gamefolk.roomfullofcats.RoomFullOfCatsApp;
import org.gamefolk.roomfullofcats.utils.JsonUtils;
import org.joda.time.Duration;

import java.io.*;
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
 * fallTime (optional)          Starting speed at which the cats fall, in milliseconds
 * catsLimit (optional)         The target number of cats of the same type to collect
 * moveLimit (optional)         The number of cats the player is allowed to remove before failing the level
 * requiredMatch (optional)     An object mapping the number of cats of a given type that are required.
 *      blue (optional)         The number of full baskets of blue cats the player must achieve to win the level
 *      gray (optional)         The number of full baskets of gray cats the player must achieve to win the level
 *      pink (optional)         The number of full baskets of pink cats the player must achieve to win the level
 *      stripe (optional)       The number of full baskets of stripe cats the player must achieve to win the level
 * glitchTypeSpawn (optional)   Used only for levels of type "glitch", specifies which of the four types of glitch cat
 *                              will spawn in the level. Currently unimplemented.
 */
public class Level {
    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());

    public final int number;
    public final int mapWidth;
    public final int mapHeight;
    public final Duration levelTime;
    public final Duration fallTime;
    public final int catsLimit;      // the target number of cats of the same type to collect
    public final String message;
    public final String title;

    private Level(int number, int mapWidth, int mapHeight, Duration levelTime, Duration fallTime, int catsLimit,
                  String message, String title) {
        this.number = number;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.levelTime = levelTime;
        this.fallTime = fallTime;
        this.catsLimit = catsLimit;
        this.message = message;
        this.title = title;
    }

    public static Level loadLevel(String path, int number) throws FileNotFoundException {
        JsonObject levelObject;
        try {
            levelObject = JsonUtils.readJsonResource(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Log.info("level: " + levelObject.get("levelTitle").asString());

        String message = levelObject.get("levelDescription").asString();
        String title = levelObject.get("levelTitle").asString();

        Duration levelTime = Duration.standardSeconds(levelObject.get("timeLimit").asLong());

        return new Builder(number, message, title)
                .mapWidth(levelObject.get("columns").asInt())
                .mapHeight(levelObject.get("rows").asInt())
                .levelTime(levelTime)
                .build();
    }

    private static class Builder {
        private static final Duration DEFAULT_FALL_TIME = Duration.millis(1000);
        private static final int DEFAULT_CATS_LIMIT = 3;

        private final String title;
        private final String message;
        private final int number;
        private int mapWidth;
        private int mapHeight;
        private Duration levelTime;
        private Duration fallTime = DEFAULT_FALL_TIME;
        private int catsLimit = DEFAULT_CATS_LIMIT;

        public Builder(int number, String message, String title) {
            this.number = number;
            this.message = message;
            this.title = title;
        }

        public Builder mapWidth(int mapWidth) {
            this.mapWidth = mapWidth;
            return this;
        }

        public Builder mapHeight(int mapHeight) {
            this.mapHeight = mapHeight;
            return this;
        }

        public Builder levelTime(Duration levelTime) {
            this.levelTime = levelTime;
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

        public Level build() {
            return new Level(number, mapWidth, mapHeight, levelTime, fallTime, catsLimit, message, title);
        }
    }
}
