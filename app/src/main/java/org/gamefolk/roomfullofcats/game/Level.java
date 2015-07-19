package org.gamefolk.roomfullofcats.game;

import com.eclipsesource.json.JsonObject;
import org.gamefolk.roomfullofcats.RoomFullOfCatsApp;
import org.joda.time.Duration;

import java.io.*;
import java.util.logging.Logger;

/**
* A class that represents in-game levels, as defined by several parameters
* @param levelTitle                     The title for the individual level, for display on level select screens etc
* @param levelDescription               The descriptive flavor text for the level that describes its goal or mechanics
* @param levelType                      An array of strings that describes the core level mechanic(s).  Available choices are "time", "moves", and "glitch".  Can combine multiple mechanics, such as ["time","moves"]
* @param rows                           The number of rows for the playfield
* @param columns                        The number of colums for the playfield
* @param timeLimit                      Time limit for the level in seconds
* @param fallTime                       Starting speed at which the cats fall, in milliseconds
* @param moveLimit                      The number of cats the player is allowed to remove before failing the level
* @param requiredScore                  The minimum score the player is required to achieve in order to win the level
* @param requiredMatch1                 The minimum number of full baskets of cat type 1 the player must achieve in order to win the level
* @param requiredMatch2                 The minimum number of full baskets of cat type 2 the player must achieve in order to win the level
* @param requiredMatch3                 The minimum number of full baskets of cat type 3 the player must achieve in order to win the level
* @param requiredMatch4                 The minimum number of full baskets of cat type 4 the player must achieve in order to win the level
* @param glitchTypeSpawn                Used only for levels of type "glitch", specifies which of the four types of glitch cat will spawn in the level
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

    public static Level loadLevel(String path) throws FileNotFoundException {
        InputStream input = RoomFullOfCatsApp.class.getResourceAsStream(path);
        if (input == null) {
            throw new FileNotFoundException("Could not find level: " + path);
        }

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

        Duration levelTime = Duration.standardSeconds(mainObject.get("timeLimit").asLong());

        // TODO: Handle more level numbers
        return new Builder(1, message, title)
                .mapWidth(mainObject.get("columns").asInt())
                .mapHeight(mainObject.get("rows").asInt())
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
