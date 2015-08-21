package org.gamefolk.roomfullofcats.game.goals;

import org.gamefolk.roomfullofcats.game.Game;

public class MoveGoal implements Goal {
    private int moveLimit;

    public MoveGoal(int moveLimit) {
        this.moveLimit = moveLimit;
    }

    @Override
    public String getDescription() {
        return String.format("You have only %d moves.", moveLimit);
    }

    @Override
    public boolean isSatisfied(Game game) {
        return game.getNumMoves() <= moveLimit;
    }
}
