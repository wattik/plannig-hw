package wumpus.agent.heuristics;

import wumpus.world.WorldState;

public interface IDistanceFactory {

    IDistance getInstance(WorldState state);
}
