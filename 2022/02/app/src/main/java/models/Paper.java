package models;

public record Paper() implements HandShape {
    @Override
    public PlayResult against(HandShape otherShape) {
        return switch (otherShape) {
            case Rock rock -> PlayResult.WIN;
            case Paper paper -> PlayResult.DRAW;
            case Scissors scissors -> PlayResult.DEFEAT;
        };
    }

    @Override
    public int getShapePoints() {
        return 2;
    }
}
