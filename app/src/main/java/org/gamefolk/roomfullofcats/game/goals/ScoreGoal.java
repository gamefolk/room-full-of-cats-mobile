package org.gamefolk.roomfullofcats.game.goals;

import org.gamefolk.roomfullofcats.game.Game;

public class ScoreGoal implements Goal {
    private int requiredScore;

    public ScoreGoal(int requiredScore) {
        this.requiredScore = requiredScore;
    }

    @Override
    public String getDescription() {
        return String.format("You need to get %d points.", requiredScore);
    }

    @Override
    public boolean isSatisfied(Game game) {
        return game.scoreProperty().get() >= requiredScore;
    }
}
