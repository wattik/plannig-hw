package wumpus.agent.rollout;

import wumpus.agent.policy.IPolicy;
import wumpus.agent.policy.NoPolicyForThisState;
import wumpus.world.*;

import javax.vecmath.Point2i;
import java.util.Random;
import java.util.Set;

public class RollOut {

    final private int trialsNumber;
    final private double gamma;
    final private Random random;

    public RollOut(int trialsNumber, double gamma, Random random) {
        this.trialsNumber = trialsNumber;
        this.gamma = gamma;
        this.random = random;
    }

    public ActionProposal getAction(WorldState state, IPolicy policy) throws NoPolicyForThisState {
        Set<Action> actions = WorldModel.getActions(state);

        double bestQestimate = Double.NEGATIVE_INFINITY;
        Action bestAction = null;
        for (Action action : actions) {
            double qEstimate = this.simQ(state, action, policy);
            if (qEstimate >= bestQestimate) {
                bestQestimate = qEstimate;
                bestAction = action;
            }
        }

        return new ActionProposal(bestAction, bestQestimate);
    }

    private double simQ(WorldState state, Action action, IPolicy policy) throws NoPolicyForThisState {
        float rewardSum = 0;
        for (int i = 0; i < this.trialsNumber; i++) {
            rewardSum += rollOut(state, action, policy);
        }
        return rewardSum / this.trialsNumber;
    }

    private double rollOut(WorldState state, Action action, IPolicy policy) throws NoPolicyForThisState {
        Outcome outcome = WorldModel.performAction(state, action, random);
        double reward = outcome.reward;
        double gamma = this.gamma;
        state = outcome.state;

        if (WorldModel.isTerminal(state)) {
            return reward;
        }

        for (int i = 1; i < state.getActionsLeft(); i++) {
            outcome = WorldModel.performAction(state, policy.getAction(state), random);
            reward += gamma * outcome.reward;
            gamma *= this.gamma;

            state = outcome.state;

            if (WorldModel.isTerminal(state)) {
                break;
            }

            CellContent[][] map = state.getMap();
            Point2i agent = state.getAgent();
            if (map[agent.x][agent.y] == CellContent.GOLD) {
                break;
            }

        }

        return reward;
    }
}
