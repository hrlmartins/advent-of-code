import com.google.common.primitives.Chars;
import models.Rucksack;

import java.util.List;

public class SolverP1 {
    public void solve(List<String> allLines) {
        var ruckSacks = allLines.stream()
                .map(rawSack -> new Rucksack(Chars.asList(rawSack.toCharArray()))).toList();

        // transform into list of priorities (or common item types)
        var prioritiesSum = ruckSacks.stream().map(rs -> {
            var firstComp = rs.firstCompartimentContent();
            firstComp.retainAll(rs.secondCompartimentContent());
            // it's an inline modifier method so the result is in the first list of the chain :)
            return firstComp.get(0);
        }).reduce(0, (sum, item) -> {
            if (Character.isLowerCase(item)) {
                return sum + (item - 'a' + 1);
            }

            return sum + (item - 'A' + 27);
        }, Integer::sum);

        System.out.printf("The sum of priorities is %d\n", prioritiesSum);
    }
}
