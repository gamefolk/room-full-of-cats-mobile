package org.gamefolk.roomfullofcats.game;

import javafx.animation.Animation;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cat {
    public final Type type;
    public final FrameAnimation sprite;

    public Cat(Type type) {
        this.type = type;
        this.sprite = new FrameAnimation(type.getWidth(), type.getHeight(), Duration.millis(1000),
                type.getFrames().toArray(new Image[type.getFrames().size()]));
        this.sprite.setCycleCount(Animation.INDEFINITE);
        this.sprite.play();
    }

    @Override
    public String toString() {
        return this.type.name();
    }

    public enum Type {
        BLUE_CAT, GRAY_CAT, PINK_CAT, STRIPE_CAT;

        private static final Random RAND = new Random();

        private List<Image> frames;

        public static Type getRandomCat() {
            return Type.values()[RAND.nextInt(Type.values().length)];
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
            return new Image(Type.class.getResource(path).toString());
        }

        public List<Image> getFrames() {
            return frames;
        }
    }
}
