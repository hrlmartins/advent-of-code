package models.commands;

public sealed abstract class Command permits Left, Right, Down, Up {
    protected int numberOfSteps;

    public Command(int numberOfSteps) {
        this.numberOfSteps = numberOfSteps;
    }

    public int getNumberOfSteps() {
        return numberOfSteps;
    }

    public String toString() {
        return this.getClass().getName() + " with " + numberOfSteps + " steps";
    }
}
