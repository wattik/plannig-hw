package wumpus.agent.heuristics;

import javax.vecmath.Point2i;

public class Manhattan implements IDistance, IDistanceFrom {

    public Manhattan() {
    }

    @Override
    public double compute(Point2i a, Point2i b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private Point2i target;

    public Manhattan(Point2i target) {
        this.target = target;
    }

    @Override
    public double compute(Point2i a) {
        return compute(a, target);
    }
}
