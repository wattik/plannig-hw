package wumpus.agent.generators;

import wumpus.agent.policy.IPolicy;
import wumpus.world.WorldState;

public interface IPolicyGenerator<T extends IPolicy> {
    T getPolicy(WorldState state) throws NoFeasiblePolicyFound;
}
