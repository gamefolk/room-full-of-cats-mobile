package org.gamefolk.roomfullofcats.game.goals;

import org.gamefolk.roomfullofcats.game.Game;

public interface Goal {
    String getDescription();
    boolean isSatisfied(Game game);
}
