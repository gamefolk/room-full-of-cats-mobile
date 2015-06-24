package org.gamefolk.roomfullofcats.game;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum CatType {
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

    private static Image loadImage(String path) {
        return new Image(CatType.class.getResource(path).toString());
    }

    public List<Image> getFrames() {
        return frames;
    }
}
