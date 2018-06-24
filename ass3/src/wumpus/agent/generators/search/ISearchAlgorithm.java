package wumpus.agent.generators.search;

import javax.vecmath.Point2i;

public interface ISearchAlgorithm<T extends Solution> {

    T search(Point2i start) throws DestinationUnreachable;

}
