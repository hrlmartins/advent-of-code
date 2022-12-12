import models.NoSquare;
import models.Node;
import models.Position;
import models.SquareNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SolverP2 {
    public void solve(List<String> allLines) {
        var rows = allLines.size();
        var cols = allLines.get(0).length();

        var graph = new SquareNode[rows][cols];

        Position startPos = initGraph(allLines, graph);

        var aPositions = new ArrayList<Position>();
        for (int row = 0; row < allLines.size(); row++) {
            for (int col = 0; col < allLines.get(row).toCharArray().length; col++) {
                if (graph[row][col].elevation() == 'a') {
                    aPositions.add(new Position(row, col));
                }
            }
        }

        List<Long> allStarterPositionSteps = aPositions.stream().map(pos -> processPath(graph, rows, cols, pos, 'E')).sorted().toList();
        System.out.printf("The number of steps needed for the best a is %d\n", allStarterPositionSteps.get(0));
    }

    long processPath(SquareNode[][] graph, int rows, int cols, Position startPos, char goal) {
        Queue<Node> toProcess = new LinkedList<>();
        var visited = new HashSet<Position>();
        visited.add(startPos);
        toProcess.offer(graph[startPos.row()][startPos.col()]);
        toProcess.offer(new NoSquare());
        var steps = 0L;

        while (!toProcess.isEmpty()) {
            var currentNode = toProcess.poll();

            if (currentNode instanceof SquareNode currentStepNode) {
                if (currentStepNode.elevation() == goal) {
                    // we reached our destination
                    return steps;
                }

                for (Position neighbourPosition : neighbourPositions(currentStepNode.pos())) {
                    if (validPosition(neighbourPosition, rows, cols) && !visited.contains(neighbourPosition)) {
                        // target is higher
                        // target - current = positive value.. of at most one
                        // target is lower
                        // target - current = negative value... anything goes... no restrictions in downward motions
                        if (elevationDiff(graph[neighbourPosition.row()][neighbourPosition.col()], currentStepNode) <= 1) {
                            toProcess.offer(graph[neighbourPosition.row()][neighbourPosition.col()]);
                            visited.add(neighbourPosition);
                        }
                    }
                }
            } else if (currentNode instanceof NoSquare && toProcess.isEmpty()) {
                // dead end... no path from the start
                return Long.MAX_VALUE;
            } else {
                // marker of level change
                steps++;
                toProcess.offer(new NoSquare());
            }
        }

        return steps;
    }

    boolean validPosition(Position neighbourPosition, int rows, int cols) {
        return neighbourPosition.row() >= 0 &&
                neighbourPosition.col() >=0 &&
                neighbourPosition.row() < rows &&
                neighbourPosition.col() < cols;
    }

    int elevationDiff(SquareNode first, SquareNode second) {
        return (first.elevation() == 'E' ? 'z' : first.elevation()) - (second.elevation()  == 'E' ? 'z' : second.elevation());
    }

    List<Position> neighbourPositions(Position start) {
        return List.of(
                new Position(start.row() + 1, start.col()),
                new Position(start.row(), start.col() + 1),
                new Position(start.row() - 1, start.col()),
                new Position(start.row(), start.col() - 1)
        );
    }

    Position initGraph(List<String> allLines, SquareNode[][] graph) {
        Position startPos = new Position(0, 0);
        for (int row = 0; row < allLines.size(); row++) {
            for (int col = 0; col < allLines.get(row).toCharArray().length; col++) {
                char elevation = allLines.get(row).charAt(col);
                graph[row][col] = new SquareNode(elevation, new Position(row, col));

                if (elevation == 'S') {
                    // hackz override
                    graph[row][col] = new SquareNode('a', new Position(row, col));
                    startPos = new Position(row, col);
                }
            }
        }

        return startPos;
    }

}
