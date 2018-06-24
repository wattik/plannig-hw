package wumpus.agent.heuristics;

import wumpus.world.WorldState;

public class ReachableDistanceFactory implements IDistanceFactory {
    @Override
    public IDistance getInstance(WorldState state) {
        return new ReachableDistance(state.getMap());
    }
}
