package wumpus.agent.generators.search;

import wumpus.agent.heuristics.IDistanceFrom;

import javax.vecmath.Point2i;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class AStarSetting<T extends Solution> implements ISearchAlgorithm<T> {

    private IDistanceFrom heuristics;
    private ITerminationChecker terminationChecker;
    private IExpander expander;
    private ISolutionFactory<T> solutionFactory;

    public AStarSetting(IDistanceFrom heuristics, ITerminationChecker terminationChecker, IExpander expander, ISolutionFactory<T> solutionFactory) {
        this.heuristics = heuristics;
        this.terminationChecker = terminationChecker;
        this.expander = expander;
        this.solutionFactory = solutionFactory;
    }

    public T search( final Point2i initPosition) throws DestinationUnreachable {

        Queue<Node> queue = new PriorityQueue<>();
        queue.add(new Node(heuristics.compute(initPosition), initPosition));
        Set<Node> visited = new HashSet<>();

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            visited.add(node);
            Point2i position = node.position;
            System.out.println(position);

            if (terminationChecker.isTerminal(position)) {
                return solutionFactory.getSolution(node);
            }

            for (Node newNode: expander.expand(node)) {
                if (!visited.contains(newNode)) {
                    newNode.setHeuristics(heuristics.compute(newNode.position));
                    queue.add(newNode);
                }
            }

        }

        throw new DestinationUnreachable();
    }

}
