package wumpus.agent.heuristics;

import wumpus.agent.generators.components.FinalPositionTermination;
import wumpus.agent.generators.components.GridWorldExpander;
import wumpus.agent.generators.components.PathDistanceExtractor;
import wumpus.agent.generators.components.PathDistanceSolution;
import wumpus.agent.generators.search.*;
import wumpus.world.CellContent;

import javax.vecmath.Point2i;

public class ReachableDistance implements IDistance {

    private CellContent[][] map;

    public ReachableDistance(CellContent[][] map) {
        this.map = map;
    }

    @Override
    public double compute(Point2i source, Point2i target) {

        IDistanceFrom manhattan = new Manhattan(target);
        ITerminationChecker termination = new FinalPositionTermination(target);
        IExpander expander = new GridWorldExpander(map);
        ISolutionFactory<PathDistanceSolution> extractor = new PathDistanceExtractor();

        ISearchAlgorithm<PathDistanceSolution> aStar = new AStarSetting<>(manhattan, termination, expander, extractor);

        try {
            return aStar.search(source).getDistance();
        } catch (DestinationUnreachable destinationUnreachable) {
            return Double.POSITIVE_INFINITY;
        }

    }
}
