package wumpus.agent.generators.search;

import wumpus.world.Action;

import javax.vecmath.Point2i;

public class Node implements Comparable<Node> {

    private final double g;
    private double f;

    public final Node previous;
    public final Action action;
    public final Point2i position;

    public Node(Node previous, Action action, Point2i position) {
        this.g = previous.g + 1;
        this.action = action;
        this.position = position;
        this.previous = previous;
    }

    void setHeuristics(double h) {
        this.f = g + h;
    }

    Node(double h, Point2i position) {
        this.g = 0;
        this.f = this.g + h;
        this.position = position;
        this.previous = null;
        this.action = null;
    }

    public boolean isInitial() {
        return this.previous == null;
    }

    @Override
    public int compareTo(Node o) {
        return Double.compare(this.f, o.f);
    }

    @Override
    public int hashCode() {
        return position.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node){
            return position.equals(((Node) obj).position);
        }
        return false;
    }
}
