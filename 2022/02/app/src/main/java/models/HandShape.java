package models;

public sealed interface HandShape permits Rock, Paper, Scissors {
    PlayResult against(HandShape otherShape);
    int getShapePoints();
}

