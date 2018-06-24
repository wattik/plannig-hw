package wumpus.agent.policy;

import wumpus.world.Action;
import wumpus.world.WorldState;

import javax.vecmath.Point2i;

public class PositionBasedPolicy implements IPolicy {

    protected Action[][] policy;
    protected Point2i target;

    public PositionBasedPolicy(Action[][] policy, Point2i target) {
        this.policy = policy;
        this.target = target;
    }

    public Action getAction(WorldState state) throws NoPolicyForThisState {
        Action action = this.policy[state.getX()][state.getY()];
        if (action == null) {
            throw new NoPolicyForThisState();
        }
        return action;
    }

    @Override
    public boolean isSatisfied(WorldState state) {
        return state.getAgent().equals(this.target);
    }

    public Action[][] getPolicy() {
        return policy;
    }

    public Point2i getTarget() {
        return target;
    }
}
