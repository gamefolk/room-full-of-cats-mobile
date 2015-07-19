package org.gamefolk.roomfullofcats.game;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;

public class FrameAnimation extends Transition {
    private final List<Image> frames;
    private final double width;
    private final double height;

    private int lastFrameIndex;

    /**
     * Creates a Sprite that will animate itself on an ImageView.
     * @param width The width (in pixels) of the sprite
     * @param height The height (in pixels) of the sprite
     * @param duration The total duration of the animation
     * @param frames The images to be used in the animation
     */
    public FrameAnimation(double width, double height, Duration duration, Image... frames) {
        this.frames = Arrays.asList(frames);
        this.width = width;
        this.height = height;
        this.lastFrameIndex = 0;

        setCycleDuration(duration);
        setInterpolator(Interpolator.LINEAR);
    }

    @Override
    protected void interpolate(double k) {
        final int frameIndex = Math.min((int) Math.floor(k * frames.size()), frames.size() - 1);
        if (frameIndex != lastFrameIndex) {
            lastFrameIndex = frameIndex;
        }
    }

    public Image getCurrentFrame() {
        return frames.get(lastFrameIndex);
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }
}
