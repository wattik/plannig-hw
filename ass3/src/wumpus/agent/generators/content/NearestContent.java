package wumpus.agent.generators.content;

import wumpus.agent.heuristics.IDistanceFactory;
import wumpus.world.CellContent;
import wumpus.world.WorldState;

import javax.vecmath.Point2i;

public class NearestContent implements ICellContentFinder {

    private IDistanceFactory distanceFactory;

    public NearestContent(IDistanceFactory distanceFactory) {
        this.distanceFactory = distanceFactory;
    }

    public Point2i getPosition(WorldState state, CellContent prototype) {
        CellContent[][] map = state.getMap();

        Point2i nearestPosition = null;
        double lowestDistance = Double.POSITIVE_INFINITY;

        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {

                if (map[x][y] == prototype) {
                    Point2i contentPosition = new Point2i(x, y);
                    double distance = distanceFactory.getInstance(state).compute(state.getAgent(), contentPosition);

                    if (distance <= lowestDistance) {
                        lowestDistance = distance;
                        nearestPosition = contentPosition;
                    }
                }

            }
        }

        return nearestPosition;
    }



}
