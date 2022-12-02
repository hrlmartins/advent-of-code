package models;

public record Scissors() implements HandShape {
    @Override
    public PlayResult against(HandShape otherShape) {
        return switch (otherShape) {
            case Paper paper -> PlayResult.WIN;
            case Rock rock -> PlayResult.DEFEAT;
            case Scissors scissors -> PlayResult.DRAW;
        };
    }

    @Override
    public int getShapePoints() {
        return 3;
    }
}
