import models.Coordinates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class SolverP2 {
    public void solve(List<String> allLines) {
        var allCoordinates = getAllCoordinates(allLines);
        var rocksAndSand = new HashSet<Coordinates>();
        buildWalls(allCoordinates, rocksAndSand);
        var lowestPoint = rocksAndSand.stream().max(Comparator.comparingInt(Coordinates::y)).get().y() + 2;

        for (int countSand = 1; ; countSand++) {
            var sandPosStart = new Coordinates(500, 0);
            var previousPos = sandPosStart;
            var currentPos = sandPosStart;

            do {
                previousPos = currentPos;
                var positionDown = new Coordinates(previousPos.x(), previousPos.y() + 1);
                var positionDownAndLeft = new Coordinates(previousPos.x() - 1, previousPos.y() + 1);
                var positionDownAndRight = new Coordinates(previousPos.x() + 1, previousPos.y() + 1);

                if (!rocksAndSand.contains(positionDown) && positionDown.y() < lowestPoint) { // try move down
                    currentPos = positionDown;
                } else if (!rocksAndSand.contains(positionDownAndLeft) && positionDownAndLeft.y() < lowestPoint) {
                    currentPos = positionDownAndLeft;
                } else if (!rocksAndSand.contains(positionDownAndRight) && positionDownAndRight.y() < lowestPoint) {
                    currentPos = positionDownAndRight;
                }
            } while(!previousPos.equals(currentPos));

            // at this point the sand has stopped at source.
            if (currentPos.equals(sandPosStart)) {
                System.out.printf("The number of sand units until stop is %d\n", countSand);
                break;
            }

            rocksAndSand.add(currentPos); // sand has stopped add to the static structure of rocks and sand
        }

    }

    private static void buildWalls(List<List<Coordinates>> allCoordinates, HashSet<Coordinates> rocks) {
        for (List<Coordinates> singleWall : allCoordinates) {
            for (int pos = 0; pos < singleWall.size() - 1; pos++) {
                var wall = singleWall.subList(pos, pos + 2);

                if (wall.get(0).x() != wall.get(1).x()) {
                    int xMin = Math.min(wall.get(0).x(), wall.get(1).x());
                    int xMax = Math.max(wall.get(0).x(), wall.get(1).x());

                    for (int i = xMin; i <= xMax; i++) {
                        rocks.add(new Coordinates(i, wall.get(0).y()));
                    }
                } else if (wall.get(0).y() != wall.get(1).y()) { // yeh, why not. Just in case
                    int yMin = Math.min(wall.get(0).y(), wall.get(1).y());
                    int yMax = Math.max(wall.get(0).y(), wall.get(1).y());

                    for (int i = yMin; i <= yMax; i++) {
                        rocks.add(new Coordinates(wall.get(0).x(), i));
                    }
                }
            }
        }
    }

    List<List<Coordinates>> getAllCoordinates(List<String> allLines) {
        var res = new ArrayList<List<Coordinates>>();
        for (String line : allLines) {
            var lineCoordinates = Arrays.stream(line.split("->")).map(String::trim).map(coordRaw -> {
                var coordinates = coordRaw.split(",");
                return new Coordinates(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]));
            }).toList();

            res.add(lineCoordinates);
        }

        return res;
    }

}
