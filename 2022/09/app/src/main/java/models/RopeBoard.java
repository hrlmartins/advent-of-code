package models;

import models.commands.*;

import java.util.*;

public class RopeBoard {

    private List<Coordinates> knots;

    private Map<Integer,Set<Coordinates>> visited;

    public RopeBoard(int knots) {
        this.knots = new ArrayList<>();
        this.visited = new HashMap<>();
        // 0 is head
        for (int knot = 0; knot < knots; knot++) {
            this.knots.add(new Coordinates(0, 0));
            this.visited.put(knot, new HashSet<>());
            this.visited.get(knot).add(new Coordinates(0, 0));
        }
    }

    public void moveHead(Command command) {
        for (int step = 0; step < command.getNumberOfSteps(); step++) {
            switch(command) {
                case models.commands.Left left -> moveKnotLeft(0);
                case models.commands.Right right -> moveKnotRight(0);
                case models.commands.Down down -> moveKnotDown(0);
                case models.commands.Up up -> moveKnotUp(0);
            }
        }
    }

    public int tailVisited(int knot) {
        return this.visited.get(knot).size();
    }

    private void moveKnotLeft(int knot) {
        if (knot >= knots.size()) return;

        var currentKnot = knots.get(knot);
        knots.set(knot, new Coordinates(currentKnot.xPos() - 1, currentKnot.yPos()));
        visitSpot(knot);
        processFollowUpKnot(knot, knot + 1);
    }

    private void moveKnotRight(int knot) {
        if (knot >= knots.size()) return;

        var currentKnot = knots.get(knot);
        knots.set(knot, new Coordinates(currentKnot.xPos() + 1, currentKnot.yPos()));
        visitSpot(knot);
        processFollowUpKnot(knot, knot + 1);
    }

    private void moveKnotUp(int knot) {
        if (knot >= knots.size()) return;

        var currentKnot = knots.get(knot);
        knots.set(knot, new Coordinates(currentKnot.xPos(), currentKnot.yPos() - 1));
        visitSpot(knot);
        processFollowUpKnot(knot, knot + 1);
    }

    private void moveKnotDown(int knot) {
        if (knot >= knots.size()) return;

        var currentKnot = knots.get(knot);
        knots.set(knot, new Coordinates(currentKnot.xPos(), currentKnot.yPos() + 1));
        visitSpot(knot);
        processFollowUpKnot(knot, knot + 1);
    }

    //
    // DIAGONALS

    private void moveKnotDiagonalUpLeft(int knot) {
        if (knot >= knots.size()) return;

        var currentKnot = knots.get(knot);
        knots.set(knot, new Coordinates(currentKnot.xPos() - 1, currentKnot.yPos() - 1));
        visitSpot(knot);
        processFollowUpKnot(knot, knot + 1);
    }

    private void moveKnotDiagonalUpRight(int knot) {
        if (knot >= knots.size()) return;

        var currentKnot = knots.get(knot);
        knots.set(knot, new Coordinates(currentKnot.xPos() + 1, currentKnot.yPos() - 1));
        visitSpot(knot);
        processFollowUpKnot(knot, knot + 1);
    }

    private void moveKnotDiagonalDownLeft(int knot) {
        if (knot >= knots.size()) return;

        var currentKnot = knots.get(knot);
        knots.set(knot, new Coordinates(currentKnot.xPos() - 1, currentKnot.yPos() + 1));
        visitSpot(knot);
        processFollowUpKnot(knot, knot + 1);
    }

    private void moveKnotDiagonalDownRight(int knot) {
        if (knot >= knots.size()) return;

        var currentKnot = knots.get(knot);
        knots.set(knot, new Coordinates(currentKnot.xPos() + 1, currentKnot.yPos() + 1));
        visitSpot(knot);
        processFollowUpKnot(knot, knot + 1);
    }

    private void processFollowUpKnot(int currentKnotIdx, int nextKnotIdx) {
        if (currentKnotIdx >= knots.size() || nextKnotIdx >= knots.size()) return;

        if (!isKnotAdjacent(currentKnotIdx, nextKnotIdx)) {
            var headKnot = knots.get(currentKnotIdx);
            var tailKnot = knots.get(nextKnotIdx);
            // let's find out how they mismatch
            if (headKnot.xPos() > tailKnot.xPos() && headKnot.yPos() > tailKnot.yPos()) {
                // if head knot is to the right and below... move diagonally doww and right
                moveKnotDiagonalDownRight(nextKnotIdx);
            } else if (headKnot.xPos() > tailKnot.xPos() && headKnot.yPos() < tailKnot.yPos()) {
                // head knot is to the right and up diagonally
                moveKnotDiagonalUpRight(nextKnotIdx);
            } else if(headKnot.xPos() < tailKnot.xPos() && headKnot.yPos() < tailKnot.yPos()) {
                // head is to the left and up diagonally
                moveKnotDiagonalUpLeft(nextKnotIdx);
            } else if (headKnot.xPos() < tailKnot.xPos() && headKnot.yPos() > tailKnot.yPos()) {
                // head is to the left and down diagonally
                moveKnotDiagonalDownLeft(nextKnotIdx);
            } else if (headKnot.xPos() > tailKnot.xPos()) {
                // head is to the right of tail
                moveKnotRight(nextKnotIdx);
            } else if(headKnot.xPos() < tailKnot.xPos()) {
                // head is to the left of tail
                moveKnotLeft(nextKnotIdx);
            } else if (headKnot.yPos() > tailKnot.yPos()) {
                // head is below tail
                moveKnotDown(nextKnotIdx);
            } else if (headKnot.yPos() < tailKnot.yPos()) {
                // head is above tail
                moveKnotUp(nextKnotIdx);
            }
        }
    }


    private void visitSpot(int knot) {
        this.visited.get(knot).add(knots.get(knot));
    }

    private boolean isKnotAdjacent(int firstKnot, int secondKnot) {
        var previousKnot = knots.get(firstKnot);
        var currentKnot = knots.get(secondKnot);

        return List.of(new Coordinates(0, 0),
                new Coordinates(0, 1),
                new Coordinates(1, 0),
                new Coordinates(1, 1),
                new Coordinates(0, -1),
                new Coordinates(-1, 0),
                new Coordinates(-1, -1),
                new Coordinates(1, -1),
                new Coordinates(-1, 1)).stream().anyMatch(adjPos -> {
                    var checkPos = new Coordinates(currentKnot.xPos() + adjPos.xPos(), currentKnot.yPos() + adjPos.yPos());
                    return checkPos.equals(previousKnot);
                });
    }
}
