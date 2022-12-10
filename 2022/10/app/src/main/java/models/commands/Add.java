package models.commands;

public final class Add extends Instruction {

    private int argument;
    public Add(int argument) {
        super(2);
        this.argument = argument;
    }

    public int getArgument() {
        return argument;
    }
}
