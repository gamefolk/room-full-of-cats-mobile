package org.gamefolk.roomfullofcats.game;

import javafx.animation.Animation;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class Cat {
    public final CatType type;
    public final FrameAnimation sprite;

    public Cat(CatType type) {
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
}
