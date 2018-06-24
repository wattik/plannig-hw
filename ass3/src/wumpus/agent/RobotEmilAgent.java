package wumpus.agent;

import tt.euclid2d.Vector;
import wumpus.world.Action;
import wumpus.world.CellContent;

import java.util.*;

import static java.lang.Thread.sleep;

public class RobotEmilAgent {
    /**
     * This method is called after when a simulation engine request next position.
     * You are given a position of the robot and the map of the environment.
     * <p>
     * The top-left corner has coordinates (0,0). You can check whether
     * there is an obstacle by querying map[y][x] == CellContent.Obstacle.
     * <p>
     * There is one gold on the map. You can query whether a position contains gold by
     * querying map[y][x] == CellContent.Gold.
     *
     * @param x the x-coordinate of the current position of robot
     * @param y the y-coordinate of the current position of robot
     * @param map the map of the environment
     * @return position to perform in the next step
     */
    private Deque<Action> plan = null;
    private Vector expectedPosition = null;
    private Vector goldPosition = null;

    Action nextStep(int x, int y, CellContent[][] map) {
        if (goldPosition == null) {
            goldPosition = findGold(map);
        }

        Vector currentPosition = new Vector(x, y);
        if (plan == null || expectedPosition == null || !currentPosition.equals(expectedPosition)) {
            System.out.println("Finding new plan.");
            constructPlan(x, y, map);

        }

        Action action = plan.pop();

        if (action == Action.EAST) {
            expectedPosition.add(new Vector(1, 0));
        } else if (action == Action.SOUTH) {
            expectedPosition.add(new Vector(0, 1));
        } else if (action == Action.WEST) {
            expectedPosition.add(new Vector(-1, 0));
        } else { // NORTH
            expectedPosition.add(new Vector(0, -1));
        }

        return action; // return one of Action.EAST, Action.WEST, Action.NORTH, Action.SOUTH
    }

    private Vector findGold(CellContent[][] map) {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                if (map[x][y] == CellContent.GOLD) {
                    return new Vector(x, y);
                }
            }
        }

        throw new RuntimeException("Have not found gold.");
    }

    class Node implements Comparable<Node> {
        Node(Node previous, Vector position, Action action) {
            this.previous = previous;
            this.position = position;
            this.g = previous.g + 1;
            this.action = action;
        }

        Node(Vector position) {
            this.position = position;
            this.previous = null;
            this.action = null;
            this.g = 1;
        }

        Node previous;
        Vector position;
        Action action;
        int g;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;

            return node.position.equals(this.position);
        }

        public int getF() {
            return this.g + (int) Math.abs(position.x - goldPosition.x) + (int) Math.abs(position.y - goldPosition.y);
        }

        @Override
        public int hashCode() {
            return 31 + ((position != null) ? position.hashCode() : 0);
        }

        @Override
        public int compareTo(Node o) {
            return Integer.compare(this.getF(), o.getF());
        }

        @Override
        public String toString() {
            return "Node{" +
                    "position=" + position +
                    '}';
        }
    }

    private void constructPlan(int x, int y, CellContent[][] map) {
        PriorityQueue<Node> queue = new PriorityQueue<>();
        Set<Node> visited = new HashSet<>();

        queue.add(new Node(new Vector(x, y)));

        while (!queue.isEmpty()) {
            try {
                sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Node node = queue.poll();

            if (node.position.equals(this.goldPosition)) {
                extractPlan(node);
                for (Action action : this.plan) {
                    System.out.println(action);
                }
                return;
            }

            visited.add(node);

            for (Node neighbour : generateNeighbours(node, map)) {
                if (!visited.contains(neighbour)) {
                    System.out.println(neighbour);
                    queue.add(neighbour);
                }
            }
        }

        throw new RuntimeException("No plan found.");
    }

    private void extractPlan(Node node) {
        Deque<Action> plan = new ArrayDeque<>();
        while (node.previous != null) {
            plan.addFirst(node.action);
            expectedPosition = node.position;
            node = node.previous;
        }

        this.plan = plan;
    }

    private List<Node> generateNeighbours(Node node, CellContent[][] map) {
        List<Node> neighbours = new LinkedList<>();
        int x = (int) node.position.x;
        int y = (int) node.position.y;

        int maxY = map.length - 1;
        int maxX = map[0].length - 1;

        if (x < maxX && map[x + 1][y] != CellContent.OBSTACLE) {
            neighbours.add(new Node(node, new Vector(x + 1, y), Action.EAST));
        }

        if (y < maxY && map[x][y + 1] != CellContent.OBSTACLE) {
            neighbours.add(new Node(node, new Vector(x, y + 1), Action.SOUTH));
        }

        if (x > 0 && map[x - 1][y] != CellContent.OBSTACLE) {
            neighbours.add(new Node(node, new Vector(x - 1, y), Action.WEST));
        }

        if (y > 0 && map[x][y - 1] != CellContent.OBSTACLE) {
            neighbours.add(new Node(node, new Vector(x, y - 1), Action.NORTH));
        }

        return neighbours;
    }
}
