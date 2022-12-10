import models.Device;
import models.commands.Add;
import models.commands.Instruction;
import models.commands.Noop;

import java.util.List;

public class SolverP2 {

    public void solve(List<String> allLines) {
        var instructions = allLines.stream().map(SolverP2::readInstruction).toList();

        var device = new Device();
        // Init registry
        device.setRegistry("X", 1);

        instructions.forEach(inst -> device.performInstruction(inst, "X"));
        System.out.printf("Total number of cycles performed %d\n", device.getCycle());

        var cycle = 0;
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 40; col++) {
                var registryValue = device.getRegistryValueAtCycle("X", cycle);
                var spritePos = List.of(registryValue - 1, registryValue, registryValue + 1);
                if (spritePos.contains(col)) {
                    System.out.print("#");
                } else {
                    System.out.print(".");
                }
                cycle++;
            }
            System.out.println();
        }

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
