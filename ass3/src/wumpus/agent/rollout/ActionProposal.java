package wumpus.agent.rollout;

import wumpus.world.Action;

public class ActionProposal {
    public final Action action;
    public final double expectedQ;

    public ActionProposal(Action action, double expectedQ) {
        this.action = action;
        this.expectedQ = expectedQ;
    }
}
