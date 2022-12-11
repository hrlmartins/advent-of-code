package models;

public class Item {

    private long worryLevel;

    private long lcm;
    public Item(long worryLevel, long lcm) {
        this.worryLevel = worryLevel;

        // HACKZ... SHOULD NOT BE PART OF THE ITEM AT ALL XD
        this.lcm = lcm;
    }

    public void setWorryLevel(long worryLevel) {
        this.worryLevel = worryLevel;
    }

    public long getWorryLevel() {
        return worryLevel;
    }

    public Item addWorry(long adder) {
        this.worryLevel += adder;
        constraintValue();
        return this;
    }

    public Item multiplyWorry(long multiplier) {
        this.worryLevel *= multiplier;
        constraintValue();
        return this;
    }

    private void constraintValue() {
        this.worryLevel %= lcm;
    }
}
