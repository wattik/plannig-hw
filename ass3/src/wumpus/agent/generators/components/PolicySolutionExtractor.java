package wumpus.agent.generators.components;

import wumpus.agent.generators.search.ISolutionFactory;
import wumpus.agent.generators.search.Node;
import wumpus.agent.generators.search.Solution;
import wumpus.world.Action;

public class PolicySolutionExtractor implements ISolutionFactory<ActionGridSolution> {

    private int width;
    private int height;

    public PolicySolutionExtractor(int width, int height) {
        this.width = width;
        this.height = height;
    }

    private Action[][] extractSolution(Node node) {
        Action[][] policy = new Action[width][height];

        Action action = node.action;
        while (!node.isInitial()) {
            node = node.previous;
            policy[node.position.getX()][node.position.getY()] = action;
            action = node.action;
        }

        return policy;
    }

    @Override
    public ActionGridSolution getSolution(Node finalNode) {
        return new ActionGridSolution(extractSolution(finalNode));
    }
}
