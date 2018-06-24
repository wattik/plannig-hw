package wumpus.agent.generators.components;

import wumpus.agent.generators.search.ITerminationChecker;
import wumpus.world.Action;

import javax.vecmath.Point2i;

public class BackToPolicyTermination implements ITerminationChecker {
    private Action[][] policy;

    public BackToPolicyTermination(Action[][] policy) {
        this.policy = policy;
    }

    @Override
    public boolean isTerminal(Point2i position) {
        return policy[position.getX()][position.getY()] != null;
    }
}
