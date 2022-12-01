import helpers.ElfProcessor;
import helpers.InputReader;
import models.Elf;

import java.util.Comparator;
import java.util.List;

public class SolverP2 {
    public void solve(List<String> allLines) {
        var elfs = ElfProcessor.getherElfs(allLines);

        System.out.printf("the sum of the 3 fattest elfs is %d\n",
                elfs.stream()
                        .sorted(Comparator.comparing(Elf::packedCalories).reversed()).toList()
                        .subList(0, 3).stream()
                        .map(Elf::packedCalories)
                        .reduce(Integer::sum)
                        .orElse(0)
        );
    }
}
