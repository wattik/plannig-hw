package wumpus.agent.generators;

import wumpus.agent.generators.components.*;
import wumpus.agent.generators.content.*;
import wumpus.agent.generators.search.*;
import wumpus.agent.heuristics.IDistanceFrom;
import wumpus.agent.heuristics.Manhattan;
import wumpus.agent.heuristics.ReachableDistanceFactory;
import wumpus.agent.policy.PositionBasedPolicy;

import wumpus.world.CellContent;
import wumpus.world.WorldState;

import javax.vecmath.Point2i;

public class DeterministicGoldPlanner implements IPolicyGenerator<PositionBasedPolicy> {

    private ICellContentFinder goldFinder = new NearestContent(new ReachableDistanceFactory());

    @Override
    public PositionBasedPolicy getPolicy(WorldState state) throws NoFeasiblePolicyFound {
        Point2i target = goldFinder.getPosition(state, CellContent.GOLD);
        Point2i agentPosition = state.getAgent();
        CellContent[][] map = state.getMap();

        System.out.println("Searching from " + agentPosition + " to " + target);

        int width = map.length;
        int height = map[0].length;

        IDistanceFrom manhattan = new Manhattan(target);
        ITerminationChecker termination = new FinalPositionTermination(target);
        IExpander expander = new GridWorldExpander(map);
        ISolutionFactory<ActionGridSolution> extractor = new PolicySolutionExtractor(width, height);

        ISearchAlgorithm<ActionGridSolution> aStar = new AStarSetting<>(manhattan, termination, expander, extractor);

        try {

            ActionGridSolution solution = aStar.search(agentPosition);
            return transformSolution(solution, target);

        } catch (DestinationUnreachable destinationUnreachable) {
            throw new NoFeasiblePolicyFound();
        }
    }

    private PositionBasedPolicy transformSolution(ActionGridSolution solution, Point2i target) {
        return new PositionBasedPolicy(solution.getActionGridPolicy(), target);
    }

}
