package wumpus.agent.generators.search;

public interface ISolutionFactory<T extends Solution> {
    T getSolution(Node finalNode);
}
