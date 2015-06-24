package org.gamefolk.roomfullofcats.game;

public class Level {
    public final int number;
    public final int mapWidth;
    public final int mapHeight;
    public final int levelTime;
    public final int fallTime;       // interval after which cats fall, in seconds
    public final int catsLimit;      // the target number of cats of the same type to collect
    public final String message;
    public final String title;

    private Level(int number, int mapWidth, int mapHeight, int levelTime, int fallTime, int catsLimit, String message, String title) {
        this.number = number;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.levelTime = levelTime;
        this.fallTime = fallTime;
        this.catsLimit = catsLimit;
        this.message = message;
        this.title = title;
    }

    public static class Builder {
        private static final int DEFAULT_FALL_TIME = 1;
        private static final int DEFAULT_CATS_LIMIT = 3;

        private String title;
        private String message;
        private int number;
        private int mapWidth;
        private int mapHeight;
        private int levelTime;
        private int fallTime = DEFAULT_FALL_TIME;
        private int catsLimit = DEFAULT_CATS_LIMIT;

        public Builder(final int number, final String message, final String title) {
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

        public Builder levelTime(int levelTime) {
            this.levelTime = levelTime;
            return this;
        }

        public Builder fallTime(int fallTime) {
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
