import models.Device;
import models.commands.Add;
import models.commands.Instruction;
import models.commands.Noop;

import java.util.List;

public class SolverP1 {
    public void solve(List<String> allLines) {
        var instructions = allLines.stream().map(SolverP1::readInstruction).toList();

        var device = new Device();
        // Init registry
        device.setRegistry("X", 1);

        instructions.forEach(inst -> device.performInstruction(inst, "X"));
        System.out.printf("Total number of cycles performed %d\n", device.getCycle());

        var sumOfCycleValues = List.of(20, 60, 100, 140, 180, 220).stream().reduce(0, (sum, cycleVal) -> {
            // we get the result of the previous complete cycle. The semantic of the device is to provide the state
            // AFTER the cycle. The problem statement wants during the cycle :)
            var valueRegistryAt = device.getRegistryValueAtCycle("X", cycleVal - 1);
            System.out.printf("Value at cycle %d: %d\n", cycleVal, valueRegistryAt);
            return sum + (cycleVal * valueRegistryAt);
        });

        System.out.printf("The sum of the registry values at cycle is: %d\n", sumOfCycleValues);
    }

    private static Instruction readInstruction(String rawInst) {
        if (rawInst.startsWith("noop")) {
            return new Noop();
        } else {
            var splitAddCmd = rawInst.split(" ");
            return new Add(Integer.parseInt(splitAddCmd[1]));
        }
    }
}
