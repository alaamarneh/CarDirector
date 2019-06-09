package com.rccardirector;
import android.graphics.Point;

import com.rccardirector.Maze;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import static com.rccardirector.App.carDirection;

public class MazePathFinder {

    private Maze maze;
    private Point source;
    private Point target;
    private boolean[][] visited;
    private Map<Point, Point> parents;

    public MazePathFinder() {}

    private MazePathFinder(final Maze maze,
                           final Point source,
                           final Point target) {
        Objects.requireNonNull(maze, "The input maze is null.");
        Objects.requireNonNull(source, "The source node is null.");
        Objects.requireNonNull(target, "The target node is null.");

        this.maze = maze;
        this.source = source;
        this.target = target;

        checkSourceNode();
        checkTargetNode();

        this.visited = new boolean[maze.getHeight()][maze.getWidth()];
        this.parents = new HashMap<>();
        this.parents.put(source, null);
    }

    public List<Point> findPath(final Maze maze,
                                final Point source,
                                final Point target) {
        return new MazePathFinder(maze, source, target).compute();
    }

    private List<Point> compute() {
        final Queue<Point> queue = new ArrayDeque<>();
        final Map<Point, Integer> distances = new HashMap<>();

        queue.add(source);
        distances.put(source, 0);

        while (!queue.isEmpty()) {
            // Removes the head of the queue.
            final Point current = queue.remove();

            if (current.equals(target)) {
                return constructPath();
            }

            for (final Point child : generateChildren(current)) {
                if (!parents.containsKey(child)) {
                    parents.put(child, current);
                    // Appends 'child' to the end of this queue.
                    queue.add(child);
                }
            }
        }

        // null means that the target node is not reachable
        // from the source node.
        return null;
    }

    private List<Point> constructPath() {
        Point current = target;
        final List<Point> path = new ArrayList<>();

        while (current != null) {
            path.add(current);
            current = parents.get(current);
        }

        Collections.<Point>reverse(path);
        return path;
    }

    private Iterable<Point> generateChildren(final Point current) {
        final Point north = new Point(current.x, current.y - 1);
        final Point south = new Point(current.x, current.y + 1);
        final Point west = new Point(current.x - 1, current.y);
        final Point east = new Point(current.x + 1, current.y);

        final List<Point> childList = new ArrayList<>(4);

        if (carDirection != App.Direction.SOUTH && maze.cellIsTraversible(north)) {
            childList.add(north);
        }

        if (carDirection != App.Direction.NORTH && maze.cellIsTraversible(south)) {
            childList.add(south);
        }

        if (carDirection != App.Direction.EAST && maze.cellIsTraversible(west)) {
            childList.add(west);
        }

        if (carDirection != App.Direction.WEST && maze.cellIsTraversible(east)) {
            childList.add(east);
        }

        return childList;
    }

    private void checkSourceNode() {
        checkNode(source,
                "The source node (" + source + ") is outside the maze. " +
                        "The width of the maze is " + maze.getWidth() + " and " +
                        "the height of the maze is " + maze.getHeight() + ".");

        if (!maze.cellIsFree(source.x, source.y)) {
            throw new IllegalArgumentException(
                    "The source node (" + source + ") is at a occupied cell.");
        }
    }

    private void checkTargetNode() {
        checkNode(target,
                "The target node (" + target + ") is outside the maze. " +
                        "The width of the maze is " + maze.getWidth() + " and " +
                        "the height of the maze is " + maze.getHeight() + ".");

        if (!maze.cellIsFree(target.x, target.y)) {
            throw new IllegalArgumentException(
                    "The target node (" + target + ") is at a occupied cell.");
        }
    }

    private void checkNode(final Point node, final String errorMessage) {
        if (node.x < 0
                || node.x >= maze.getWidth()
                || node.y < 0
                || node.y >= maze.getHeight()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void main(String[] args) {
        int[][] mazePlan = {
                {0,1,0,0,0,0,0,0},
                {0,0,0,1,0,0,0,0},
                {0,0,0,0,0,0,0,0},
                {1,1,1,0,0,0,0,0},
                {0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0},
        };

        boolean[][] maze2 = new boolean[mazePlan.length][mazePlan[0].length];

        for (int i = 0; i < maze2.length; ++i) {
            for (int j = 0; j < maze2[i].length; ++j) {
                maze2[i][j] = mazePlan[i][j] > 0;
            }
        }

        final Maze maze = new Maze(maze2);
        final Point source = new Point(); // Same as new Point(0, 0):
        final Point target = new Point(7, 5);


        long startTime = System.nanoTime();
        final List<Point> path = new MazePathFinder().findPath(maze,
                source,
                target);
        long endTime  = System.nanoTime();

        System.out.printf("BFS maze finder in %d milliseconds.\n",
                (endTime - startTime) / 1_000_000L);

        System.out.println("Shortest path length: " + (path.size() - 1));
        System.out.println(maze.withPath(path));
    }
}