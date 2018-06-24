package wumpus.agent.generators.components;

import wumpus.agent.generators.search.Solution;
import wumpus.agent.policy.PositionBasedPolicy;
import wumpus.world.Action;

public class ActionGridSolution extends Solution {
    private Action[][] policy;

    ActionGridSolution(Action[][] policy) {
        this.policy = policy;
    }

    public Action[][] getActionGridPolicy() {
        return policy;
    }
}
