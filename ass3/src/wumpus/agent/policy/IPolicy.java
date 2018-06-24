package wumpus.agent.policy;

import wumpus.world.Action;
import wumpus.world.WorldState;

public interface IPolicy {
    public Action getAction(WorldState state) throws NoPolicyForThisState;

    boolean isSatisfied(WorldState state);
}
