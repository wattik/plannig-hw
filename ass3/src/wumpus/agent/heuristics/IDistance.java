package wumpus.agent.heuristics;

import javax.vecmath.Point2i;

public interface IDistance {

    double compute(Point2i a, Point2i b);

}
