package wumpus.agent.generators.components;

import wumpus.agent.generators.search.Solution;

public class PathDistanceSolution extends Solution {

    private double distance;

    public PathDistanceSolution(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }
}
