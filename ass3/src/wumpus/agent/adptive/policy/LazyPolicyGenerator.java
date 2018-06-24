package wumpus.agent.adptive.policy;

import wumpus.agent.generators.DeterministicGoldPlanner;
import wumpus.agent.generators.IPolicyGenerator;
import wumpus.agent.generators.NoFeasiblePolicyFound;
import wumpus.agent.policy.PositionBasedPolicy;
import wumpus.world.WorldState;

public class LazyPolicyGenerator implements IPolicyGenerator<LazyPolicy> {

    private IPolicyGenerator<PositionBasedPolicy> planner;

    public LazyPolicyGenerator() {
        this.planner = new DeterministicGoldPlanner();
    }

    @Override
    public LazyPolicy getPolicy(WorldState state) throws NoFeasiblePolicyFound {
        PositionBasedPolicy policy = planner.getPolicy(state);
        return new LazyPolicy(policy.getPolicy(), policy.getTarget());
    }
}
