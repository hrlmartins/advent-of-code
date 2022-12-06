import helpers.Processor;
import models.CrateStacks;
import models.MoveInstructions;

import java.util.List;

public class SolverP2 {
    public void solve(List<String> allLines) {
        var breakPointPos = Processor.findBreakPos(allLines);
        var cratesRaw = allLines.subList(0, breakPointPos);
        var commands = allLines.subList(breakPointPos + 1, allLines.size());
        var stacksCount = Processor.extractNumberOfStacks(cratesRaw);
        var cratesOnly = cratesRaw.subList(0, cratesRaw.size() - 1);


        List<List<String>> crateList = Processor.extractCrateList(stacksCount, cratesOnly);
        List<MoveInstructions> moveInstructions = commands.stream().map(Processor::extractMoveInstructions).toList();

        CrateStacks crateStacks = new CrateStacks(stacksCount);

        for (int cratesRows = crateList.size() - 1; cratesRows >= 0; cratesRows--) {
            var stacks = crateList.get(cratesRows);
            for (int stackPos = 0; stackPos < stacks.size(); stackPos++) {
                crateStacks.addCrateToStack(stacks.get(stackPos), stackPos + 1);
            }
        }


        for (MoveInstructions moveInstruction : moveInstructions) {
            crateStacks.moveOrderlyFromTo(moveInstruction.amount(), moveInstruction.fromStack(), moveInstruction.toStack());
        }

        System.out.println();
        for (int i = 1; i <= stacksCount; i++) {
            System.out.print(crateStacks.peekTop(i));
        }
    }
}
