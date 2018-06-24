package wumpus.agent.generators;

import wumpus.agent.generators.components.ActionGridSolution;
import wumpus.agent.generators.components.BackToPolicyTermination;
import wumpus.agent.generators.components.GridWorldExpander;
import wumpus.agent.generators.components.PolicyUpdater;
import wumpus.agent.generators.search.*;
import wumpus.agent.heuristics.IDistanceFrom;
import wumpus.agent.heuristics.Manhattan;
import wumpus.agent.policy.PositionBasedPolicy;
import wumpus.world.Action;
import wumpus.world.CellContent;
import wumpus.world.WorldState;

import javax.vecmath.Point2i;

public class Replanner implements IPolicyGenerator<PositionBasedPolicy> {

    private Action[][] policy;
    private Point2i target;

    public Replanner(Action[][] policy, Point2i target) {
        this.policy = policy;
        this.target = target;
    }

    @Override
    public PositionBasedPolicy getPolicy(WorldState state) throws NoFeasiblePolicyFound {
        Point2i agentPosition = state.getAgent();
        CellContent[][] map = state.getMap();

        System.out.println("Searching from " + agentPosition + " to " + target);

        IDistanceFrom manhattan = new Manhattan(target);
        ITerminationChecker termination = new BackToPolicyTermination(policy);
        IExpander expander = new GridWorldExpander(map);
        ISolutionFactory<ActionGridSolution> extractor = new PolicyUpdater(policy);

        ISearchAlgorithm<ActionGridSolution> aStar = new AStarSetting<>(manhattan, termination, expander, extractor);

        try {

            ActionGridSolution solution = aStar.search(agentPosition);
            return transformSolution(solution);

        } catch (DestinationUnreachable destinationUnreachable) {
            throw new NoFeasiblePolicyFound();
        }
    }

    private PositionBasedPolicy transformSolution(ActionGridSolution solution) {
        return new PositionBasedPolicy(solution.getActionGridPolicy(), target);
    }
}
