package wumpus.agent.generators.search;

import wumpus.world.Action;
import wumpus.world.CellContent;

import javax.vecmath.Point2i;
import java.util.Set;

public interface IExpander {
    Set<Node> expand(Node node);
}
