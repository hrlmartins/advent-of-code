package models.commands;

public sealed abstract class Instruction permits Add, Noop {

    protected int numberOfCycles;

    public Instruction(int numberOfCycles) {
        this.numberOfCycles = numberOfCycles;
    }

    public int getNumberOfCycles() {
        return numberOfCycles;
    }
}
