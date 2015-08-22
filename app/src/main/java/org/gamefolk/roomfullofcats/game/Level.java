package org.gamefolk.roomfullofcats.game;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.sun.javafx.geom.Dimension2D;
import org.gamefolk.roomfullofcats.RoomFullOfCatsApp;
import org.gamefolk.roomfullofcats.Settings;
import org.gamefolk.roomfullofcats.game.goals.Goal;
import org.gamefolk.roomfullofcats.game.goals.MatchGoal;
import org.gamefolk.roomfullofcats.game.goals.MoveGoal;
import org.gamefolk.roomfullofcats.game.goals.ScoreGoal;
import org.gamefolk.roomfullofcats.utils.JsonUtils;
import org.joda.time.Duration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
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
    public static final int DEFAULT_FALL_TIME = 1000;
    public static final int DEFAULT_CATS_LIMIT = 4;
    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());
    public final int number;

    public final String title;
    public final String description;
    public final Duration timeLimit;
    public final Duration fallTime;
    public final int catsLimit;
    public final List<Goal> goals;
    public Dimension2D dimensions;
    private Status status;

    private Level(int number, String title, String description, Dimension2D dimensions, Duration timeLimit,
                  Duration fallTime, int catsLimit, List<Goal> goals, Status status) {
        this.number = number;
        this.title = title;
        this.description = description;
        this.timeLimit = timeLimit;
        this.fallTime = fallTime;
        this.catsLimit = catsLimit;
        this.dimensions = dimensions;
        this.goals = goals;

        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        JsonObject progress = Settings.INSTANCE.getJson("progress", new JsonObject()).asObject();
        progress.add(title, status.name());
        Settings.INSTANCE.putJson("progress", progress);
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

        JsonObject progress = Settings.INSTANCE.getJson("progress", new JsonObject()).asObject();
        Status status = Status.valueOf(progress.getString(title, Status.UNPLAYED.name()));
        levelBuilder.addStatus(status);

        int width = levelObject.get("columns").asInt();
        int height = levelObject.get("rows").asInt();
        levelBuilder = levelBuilder.mapDimensions(width, height);

        Duration levelTime = Duration.standardSeconds(levelObject.get("timeLimit").asLong());
        levelBuilder = levelBuilder.levelTime(levelTime);

        Duration fallTime = Duration.millis(levelObject.getLong("fallTime", DEFAULT_FALL_TIME));
        levelBuilder = levelBuilder.fallTime(fallTime);

        int catsLimit = levelObject.getInt("catsLimit", DEFAULT_CATS_LIMIT);
        levelBuilder = levelBuilder.catsLimit(catsLimit);

        int requiredScore = levelObject.get("requiredScore").asInt();
        levelBuilder = levelBuilder.addGoal(new ScoreGoal(requiredScore));

        JsonValue moveLimit = levelObject.get("moveLimit");
        if (moveLimit != null) {
            levelBuilder = levelBuilder.addGoal(new MoveGoal(moveLimit.asInt()));
        }

        JsonValue requiredMatch = levelObject.get("requiredMatch");
        if (requiredMatch != null) {
            Map<Cat.Type, Integer> matches = new HashMap<>();
            JsonValue blueMatches = requiredMatch.asObject().get("blue");
            if (blueMatches != null) {
                matches.put(Cat.Type.BLUE_CAT, blueMatches.asInt());
            }

            JsonValue grayMatches = requiredMatch.asObject().get("gray");
            if (grayMatches != null) {
                matches.put(Cat.Type.GRAY_CAT, grayMatches.asInt());
            }

            JsonValue pinkMatches = requiredMatch.asObject().get("pink");
            if (pinkMatches != null) {
                matches.put(Cat.Type.PINK_CAT, pinkMatches.asInt());
            }

            JsonValue stripeMatches = requiredMatch.asObject().get("stripe");
            if (stripeMatches != null) {
                matches.put(Cat.Type.STRIPE_CAT, grayMatches.asInt());
            }

            levelBuilder = levelBuilder.addGoal(new MatchGoal(matches));
        }

        Log.info("level: " + title);

        return levelBuilder.build();
    }

    public enum Status {
        WON, UNPLAYED, LOST
    }

    private static class Builder {
        private final int number;
        private final String title;
        private final String description;
        private Dimension2D mapDimensions;
        private Duration levelTime;
        private Duration fallTime;
        private int catsLimit;
        private List<Goal> goals;
        private Status status;

        public Builder(int number, String title, String description) {
            this.number = number;
            this.description = description;
            this.title = title;
            this.goals = new ArrayList<>();
        }

        public Builder addStatus(Status status) {
            this.status = status;
            return this;
        }

        public Builder mapDimensions(int width, int height) {
            this.mapDimensions = new Dimension2D(width, height);
            return this;
        }

        public Builder levelTime(Duration levelTime) {
            this.levelTime = levelTime;
            return this;
        }

        public Builder addGoal(Goal goal) {
            this.goals.add(goal);
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
            return new Level(number, title, description, mapDimensions, levelTime, fallTime, catsLimit, goals, status);
        }
    }
}
