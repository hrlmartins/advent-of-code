import models.ElfGroup;
import models.ElfSection;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class SolverP1 {
    public void solve(List<String> allLines) {
        // each line has two section ranges. One for each pair of elf
        var sections =
                allLines.stream().map(this::processSectionInput)
                        .filter(this::fullyIntersects).toList();

        System.out.printf("The number of complete overlaps is %d", sections.size());
    }

    boolean fullyIntersects(ElfGroup group) {
        var sortedByRangeStart =
                group.groupSections().stream().sorted((first, second) -> {
                    if (first.start() == second.start()) {
                        return second.end() - first.end();
                    }

                    return first.start() - second.start();
                }).toList();
        // We know at this time we only have pairs of two. Let's use that to our advantage
        return sortedByRangeStart.get(1).end() <= sortedByRangeStart.get(0).end();
    }

    ElfGroup processSectionInput(String sectionRaw) {
        var sectionPerElf = Arrays.stream(sectionRaw.split(",")).toList();

        var pairSections = sectionPerElf.stream().map(elfSection -> {
           var range = Arrays.stream(elfSection.split("-")).toList();
           return new ElfSection(Integer.parseInt(range.get(0)), Integer.parseInt(range.get(1)));
        }).toList();

        return new ElfGroup(pairSections);
    }
}
