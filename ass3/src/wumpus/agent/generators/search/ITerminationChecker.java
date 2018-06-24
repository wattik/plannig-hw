package wumpus.agent.generators.search;

import javax.vecmath.Point2i;

public interface ITerminationChecker {
    boolean isTerminal(Point2i position);
}
