import helpers.ElfProcessor;
import models.Elf;

import java.util.Comparator;
import java.util.List;

public class SolverP1 {
    public void solve(List<String> allLines) {
        var elfs = ElfProcessor.getherElfs(allLines);

        System.out.printf("The fattest elf is %d\n",
                elfs.stream()
                        .max(Comparator.comparing(Elf::packedCalories))
                        .orElse(new Elf(0))
                        .packedCalories()
        );
    }
}
