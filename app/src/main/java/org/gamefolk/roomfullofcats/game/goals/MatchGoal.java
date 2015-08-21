package org.gamefolk.roomfullofcats.game.goals;

import org.gamefolk.roomfullofcats.game.Cat;
import org.gamefolk.roomfullofcats.game.Game;

import java.util.Map;

public class MatchGoal implements Goal {
    private Map<Cat.Type, Integer> requiredMatches;

    public MatchGoal(Map<Cat.Type, Integer> requiredMatches) {
        this.requiredMatches = requiredMatches;
    }

    @Override
    public String getDescription() {
        // TODO: Make this print out nicer.
        StringBuilder description = new StringBuilder("You need to get ");
        for (Map.Entry<Cat.Type, Integer> entry : requiredMatches.entrySet()) {
            Cat.Type type = entry.getKey();
            int num = entry.getValue();

            description.append(String.format("%d %s match", num, type.toString()));
            if (num > 1) {
                description.append("es");
            }
        }
        return description.toString();
    }

    @Override
    public boolean isSatisfied(Game game) {
        Map<Cat.Type, Integer> currentMatches = game.getNumMatches();
        for (Map.Entry<Cat.Type, Integer> entry : currentMatches.entrySet()) {
            Cat.Type type = entry.getKey();
            int requiredMatchesForType = requiredMatches.getOrDefault(type, 0);
            if (entry.getValue() < requiredMatchesForType) {
                return false;
            }
        }
        return true;
    }
}
