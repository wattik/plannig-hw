package wumpus.agent;

import wumpus.agent.adptive.policy.LazyPolicy;
import wumpus.agent.adptive.policy.LazyPolicyGenerator;
import wumpus.agent.generators.DeterministicGoldPlanner;
import wumpus.agent.generators.IPolicyGenerator;
import wumpus.agent.generators.NoFeasiblePolicyFound;
import wumpus.agent.policy.IPolicy;
import wumpus.agent.policy.NoPolicyForThisState;
import wumpus.agent.policy.PositionBasedPolicy;
import wumpus.agent.rollout.ActionProposal;
import wumpus.agent.rollout.RollOut;
import wumpus.world.*;
import wumpus.world.WorldState;

import java.util.Random;

import static java.lang.Thread.sleep;

public class Agent implements WorldModel.Agent {

    private static final int ACTION_TRIALS = 100;
    private static final double GAMMA = 1;
    
    private final Random random;

    private final RollOut rollout;
    private IPolicy policy;

    private IPolicyGenerator<LazyPolicy> policyGenerator;

    public Agent() {
        this.policyGenerator = new LazyPolicyGenerator();
        this.random = new Random(42);
        this.rollout = new RollOut(ACTION_TRIALS, GAMMA, this.random);

    }

    /**
     * This method is called when the simulation engine requests the next action.
     * You are given a state of the world that consists of position of agent, positions of wumpuses, and the map of world.
     *
     * The top-left corner has coordinates (0,0).
     *
     * You can check the current position of agent through state.getAgent(), the positions of all
     * wumpuses can be obtained via state.getWumpuses() and the map of world through state.getMap().
     *
     *
     * You can check whether there is an obstacle on a particular cell of the map
     * by querying state.getMap()[x][y] == CellContent.OBSTACLE.
     *
     * There is one gold on the map. You can query whether a position contains gold by
     * querying state.getMap()[x][y] == CellContent.GOLD.
     *
     * Further, there are several pits on the map. You can query whether a position contains pit by
     * querying state.getMap()[x][y] == CellContent.PIT.
     *
     * @return action to perform in the next step
     */
    public Action nextStep(WorldState state) {
        if (this.policy == null || this.policy.isSatisfied(state)) {
            try {
                this.policy = policyGenerator.getPolicy(state);
            } catch (NoFeasiblePolicyFound noFeasiblePolicyFound) {
               throw new RuntimeException(noFeasiblePolicyFound);
            }
        }

        try {
            ActionProposal actionProposal = this.rollout.getAction(state, this.policy);
            if (actionProposal.expectedQ < 0) {
                System.out.println("NEGATIVE Q expected");
            }
            System.out.println("Rollout executed. Go: " + actionProposal.action);
            return actionProposal.action;
        } catch (NoPolicyForThisState ignored) {
            throw new RuntimeException("Should never happen as the policy is lazy;");
        }
    }

}
