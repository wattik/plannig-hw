package wumpus.agent.generators.components;

import wumpus.agent.generators.search.ISolutionFactory;
import wumpus.agent.generators.search.Node;
import wumpus.agent.generators.search.Solution;
import wumpus.world.Action;

public class PolicyUpdater implements ISolutionFactory<ActionGridSolution> {
    private Action[][] policy;

    public PolicyUpdater(Action[][] policy) {
        this.policy = policy;
    }

    @Override
    public ActionGridSolution getSolution(Node node) {
        Action action = node.action;
        while (!node.isInitial()) {
            node = node.previous;
            policy[node.position.getX()][node.position.getY()] = action;
            action = node.action;
        }

        return new ActionGridSolution(policy);
    }
}
