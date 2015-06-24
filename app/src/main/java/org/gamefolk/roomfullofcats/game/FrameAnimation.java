package org.gamefolk.roomfullofcats.game;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;

public class FrameAnimation extends Transition {
    private final List<Image> frames;
    private final ImageView imageView;
    private final double width;
    private final double height;

    private int lastFrameIndex;

    /**
     * Creates a Sprite that will animate itself on an ImageView.
     * @param imageView The image view to animate the sprite on
     * @param duration The total duration of the animation
     * @param width The width (in pixels) of the sprite
     * @param height The height (in pixels) of the sprite
     * @param frames The images to be used in the animation
     */
    public FrameAnimation(ImageView imageView, Duration duration, double width, double height, Image... frames) {
        this.frames = Arrays.asList(frames);
        this.imageView = imageView;
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
            if (imageView != null) {
                imageView.setImage(frames.get(frameIndex));
            }
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
