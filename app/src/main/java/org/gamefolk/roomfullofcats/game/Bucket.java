package org.gamefolk.roomfullofcats.game;

import javafx.scene.image.Image;
import javafx.util.Duration;

public class Bucket {
    public Cat.Type type = null;
    public int things = 0;
    public FrameAnimation sprite;

    public Bucket(Cat.Type type) {
        this.type = type;
        this.sprite = new FrameAnimation(type.getWidth(), type.getHeight(), Duration.millis(1000),
                type.getFrames().toArray(new Image[type.getFrames().size()]));
    }
}

