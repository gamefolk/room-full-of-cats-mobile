package org.gamefolk.roomfullofcats.game;

import org.joda.time.Duration;

import java.util.Timer;
import java.util.TimerTask;

public class CountdownTimer {
    public static final Duration DEFAULT_RESOLUTION = Duration.millis(10);

    private Duration duration;
    private boolean paused;
    private long resolution;
    private long time;
    private TimerTask timerTask;
    private Timer timer;
    private boolean running;

    public static class Builder {
        private Duration resolution = DEFAULT_RESOLUTION;
        private Duration duration;

        public Builder(Duration duration) {
            this.duration = duration;
        }

        public Builder setResolution(Duration resolution) {
            this.resolution = resolution;
            return this;
        }

        public CountdownTimer build() {
            return new CountdownTimer(resolution, duration);
        }
    }

    private CountdownTimer(Duration resolution, Duration duration) {
        this.duration = duration;
        this.resolution = resolution.getMillis();
        paused = true;
        timer = new Timer("CountdownTimer", true);

        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!paused) {
                    update();
                }
            }
        };
    }

    private void update() {
        time += resolution;
    }

    public void stop() {
        paused = true;
    }

    public void start() {
        paused = false;
        if (!running) {
            running = true;
            timer.scheduleAtFixedRate(timerTask, resolution, resolution);
        }
    }

    public long getRemainingTime() {
        return duration.minus(time).getMillis();
    }
}
