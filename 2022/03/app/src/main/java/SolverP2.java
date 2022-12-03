import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import com.google.common.primitives.Chars;
import models.Rucksack;

import java.util.List;

public class SolverP2 {
    public void solve(List<String> allLines) {
        var ruckSacks = allLines.stream()
                .map(rawSack -> new Rucksack(Chars.asList(rawSack.toCharArray()))).toList();


        var prioritySum = Streams.stream(Iterables.partition(ruckSacks, 3))
                .map(group -> {
                    var firstRuckSack = group.get(0).fullContent();
                    firstRuckSack.retainAll(group.get(1).fullContent());
                    firstRuckSack.retainAll(group.get(2).fullContent());
                    // it's an inline modifier method so the result is in the first list of the chain :)
                    return firstRuckSack.get(0);
                }).reduce(0, (sum, item) -> {
                    if (Character.isLowerCase(item)) {
                        return sum + (item - 'a' + 1);
                    }
                    return sum + (item - 'A' + 27);
                }, Integer::sum);

        System.out.printf("The sum of common items between groups is %d", prioritySum);
    }
}
