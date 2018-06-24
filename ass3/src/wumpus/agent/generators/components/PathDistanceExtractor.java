package wumpus.agent.generators.components;

import wumpus.agent.generators.search.ISolutionFactory;
import wumpus.agent.generators.search.Node;

public class PathDistanceExtractor implements ISolutionFactory<PathDistanceSolution> {
    @Override
    public PathDistanceSolution getSolution(Node node) {
        double distance = 0;

        while (!node.isInitial()) {
            distance += 1;
            node = node.previous;
        }

        return new PathDistanceSolution(distance);
    }
}
