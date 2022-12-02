package models;

public record Rock() implements HandShape {
    @Override
    public PlayResult against(HandShape otherShape) {
        return switch (otherShape) {
            case Rock rock -> PlayResult.DRAW;
            case Paper paper -> PlayResult.DEFEAT;
            case Scissors scissors -> PlayResult.WIN;
        };
    }

    @Override
    public int getShapePoints() {
        return 1;
    }
}
