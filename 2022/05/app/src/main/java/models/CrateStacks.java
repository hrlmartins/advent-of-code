package models;

import java.util.*;

public class CrateStacks {
    List<Deque<String>> crateStacks;

    public CrateStacks(int stacksCount) {
        crateStacks = new ArrayList<>(stacksCount + 1);

        for (int i = 0; i < stacksCount + 1; i++) {
            crateStacks.add(new ArrayDeque<>(50));
        }
    }

    public void addCrateToStack(String crate, int stackIndex) {
        if (crate.isEmpty() || crate.isBlank()) return;
        crateStacks.get(stackIndex).push(crate);
    }

    public void moveCrateFromto(int sourceIndex, int toIndex) {
        var sourceCrate = crateStacks.get(sourceIndex).pop();
        crateStacks.get(toIndex).push(sourceCrate);
    }

    public void moveOrderlyFromTo(int amount, int sourceIndex, int toIndex) {
        var tmpList = new ArrayList<String>();
        for (int i = 0; i < amount; i++) {
            tmpList.add(crateStacks.get(sourceIndex).pop());
        }
        //hackz
        Collections.reverse(tmpList);

        for (String crate : tmpList) {
            crateStacks.get(toIndex).push(crate);
        }
    }

    public String peekTop(int stackIndex) {
        return crateStacks.get(stackIndex).peek();
    }

    public void printStack() {
        System.out.println(crateStacks);
    }
}
