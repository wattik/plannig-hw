package wumpus.agent.generators.components;

import wumpus.agent.generators.search.ITerminationChecker;

import javax.vecmath.Point2i;

public class FinalPositionTermination implements ITerminationChecker {

    private Point2i finalPosition;

    public FinalPositionTermination(Point2i finalPosition) {
        this.finalPosition = finalPosition;
    }

    @Override
    public boolean isTerminal(Point2i position) {
        return position.equals(finalPosition);
    }
}
