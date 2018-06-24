package wumpus.agent.generators.content;

import wumpus.world.CellContent;
import wumpus.world.WorldState;

import javax.vecmath.Point2i;

public interface ICellContentFinder {

    public Point2i getPosition(WorldState state, CellContent prototype);

}
