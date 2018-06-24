package wumpus.agent.adptive.policy;

import wumpus.agent.generators.IPolicyGenerator;
import wumpus.agent.generators.NoFeasiblePolicyFound;
import wumpus.agent.generators.Replanner;
import wumpus.agent.policy.NoPolicyForThisState;
import wumpus.agent.policy.PositionBasedPolicy;
import wumpus.world.Action;
import wumpus.world.WorldState;

import javax.vecmath.Point2i;

public class LazyPolicy extends PositionBasedPolicy {

    public LazyPolicy(Action[][] policy, Point2i target) {
        super(policy, target);
    }

    @Override
    public Action getAction(WorldState state) {
        try {
            return super.getAction(state);
        } catch (NoPolicyForThisState noPolicyForThisState) {
            replan(state);
            try {
                return super.getAction(state);
            } catch (NoPolicyForThisState noPolicyForThisState2) {
                throw new RuntimeException("Should never happen as the policy is updated now");
            }
        }
    }

    private void replan(WorldState state) {
        try {
            Action[][] policyCopy = deepArrayCopy(policy);
            IPolicyGenerator<PositionBasedPolicy> replanner = new Replanner(policyCopy, this.target);
            PositionBasedPolicy policy = replanner.getPolicy(state);
            this.policy = policy.getPolicy();

        } catch (NoFeasiblePolicyFound ignored){
            throw new RuntimeException("This should never happen as the agent had to get to the position somehow and" +
                    " there already is a working policy from there.");
        }
    }

    private Action[][] deepArrayCopy(Action[][] actions){
        Action[][] copiedPolicy = actions.clone();
        for (int i = 0; i < copiedPolicy.length; i++) {
            copiedPolicy[i] = copiedPolicy[i].clone();
        }
        return copiedPolicy;
    }
}
