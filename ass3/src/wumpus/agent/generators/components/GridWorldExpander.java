package wumpus.agent.generators.components;

import wumpus.agent.generators.search.IExpander;
import wumpus.agent.generators.search.Node;
import wumpus.world.Action;
import wumpus.world.CellContent;

import javax.vecmath.Point2i;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class GridWorldExpander implements IExpander {
    private CellContent[][] map;

    public GridWorldExpander(CellContent[][] map) {
        this.map = map;
    }

    private boolean isPositionValid(Point2i p) {
        int width = map.length;
        int height = map[0].length;

        int x = p.x;
        int y = p.y;

        // is inside the box?
        if (x < 0 || x >= width) {
            return false;
        }

        if (y < 0 || y >= height) {
            return false;
        }

        // is accessible
        if (map[x][y] == CellContent.OBSTACLE || map[x][y] == CellContent.PIT) {
            return false;
        }

        return true;
    }

    private Set<Action> getActions(Point2i position) {
        if (map[position.getX()][position.getY()] == CellContent.EMPTY || map[position.getX()][position.getY()] == CellContent.GOLD) {
            Set<Action> actions = new LinkedHashSet<>();
            actions.add(Action.NORTH);
            actions.add(Action.SOUTH);
            actions.add(Action.EAST);
            actions.add(Action.WEST);
            return actions;
        }

        return new LinkedHashSet<>();
    }

    @Override
    public Set<Node> expand(Node node) {
        Point2i position = node.position;

        Set<Node> successors = new HashSet<>();
        for (Action action : getActions(position)) {
            Point2i newPosition = action.apply(position);
            if (this.isPositionValid(newPosition)) {
                successors.add(new Node(node, action, newPosition));
            }
        }

        return successors;
    }
}
