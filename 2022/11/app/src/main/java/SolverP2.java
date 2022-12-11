import models.Item;
import models.Monkey;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class SolverP2 {
    public void solve(List<String> allLines) {

        var monkeys = new ArrayList<Monkey>();

        // get lcm of all dividers
        var lcm = 1L;
        for (int lcmIndex = 3; lcmIndex < allLines.size(); lcmIndex += 7) {
            var divisibleBy = Integer.parseInt(allLines.get(lcmIndex).split("by")[1].trim());
            lcm *= divisibleBy;
        }

        for (int lineIdx = 0; lineIdx < allLines.size(); lineIdx += 7) {
            var line = allLines.get(lineIdx);
            if (line.startsWith("Monkey")) {
                var monkeyIdx = Integer.parseInt(line.split(" ")[1].charAt(0) + "");
                /// from here on out in order
                var startingItems = fetchMonkeyItems(allLines, lineIdx, lcm);
                var updateFunction = fetchUpdateWorry(allLines, lineIdx);
                var throwMonkeyFunction = fetchThrowMonkey(allLines, lineIdx);

                monkeys.add(new Monkey(monkeyIdx, new LinkedList<>(startingItems), updateFunction, throwMonkeyFunction));
            }
        }

        var visits = new ArrayList<Long>(monkeys.size());
        for (int i = 0; i < monkeys.size(); i++) {
            visits.add(0l);
        }


        for (int i = 0; i < 10000; i++) {
            for (Monkey monkey : monkeys) {
                if (!monkey.items().isEmpty()) {
                    // let's play
                    while(!monkey.items().isEmpty()) {
                        var item = monkey.items().poll();
                        var newItem = monkey.updateFunction().apply(item);
                        // update number of inspects for monkeys
                        visits.set(monkey.idx(), visits.get(monkey.idx()) + 1);
                        monkeys.get(monkey.throwFunction().apply(newItem)).items().add(newItem);
                    }
                }
            }
        }

        List<Long> sorted = visits.stream().sorted().toList();
        var mostActive = sorted.subList(sorted.size() - 2, sorted.size());
        System.out.println(mostActive.get(0) * mostActive.get(1));
    }
    List<Item> fetchMonkeyItems(List<String> allLines, int lineIdx, long lcm) {
        return Arrays.stream(allLines.get(lineIdx + 1)
                .split(":")[1]
                .split(",")).map(rawItem -> new Item(Long.parseLong(rawItem.trim()), lcm)).toList();
    }

    Function<Item, Item> fetchUpdateWorry(List<String> allLines, int lineIdx) {
        var updateOperationRaw =
                allLines.get(lineIdx + 2)
                        .split("=")[1]
                        .trim()
                        .split("old")[1].trim();

        var operation = updateOperationRaw.charAt(0) + "";
        var value = updateOperationRaw.split(" ");


        if (operation.equals("*")) {
            // multiply
            return item -> item.multiplyWorry(
                    (updateOperationRaw.length() > 1 ?  Long.parseLong(value[1].trim()) : item.getWorryLevel()));
        } else {
            // addition
            return item -> item.addWorry(
                    (updateOperationRaw.length() > 1 ?  Long.parseLong(value[1].trim()) : item.getWorryLevel()));
        }
    }
    Function<Item, Integer> fetchThrowMonkey(List<String> allLines, int lineIdx) {
        var divisbleBy = Integer.parseInt(allLines.get(lineIdx + 3).split("by")[1].trim());
        var monkeyTrueSplit = allLines.get(lineIdx + 4).split(" ");
        var monkeyTrueValue = Integer.parseInt(monkeyTrueSplit[monkeyTrueSplit.length - 1].trim());
        var monkeyFalseSplit = allLines.get(lineIdx + 5).split(" ");
        var monkeyFalseValue = Integer.parseInt(monkeyFalseSplit[monkeyFalseSplit.length - 1].trim());
        return item -> {
            if (Long.remainderUnsigned(item.getWorryLevel(), divisbleBy) == 0) {
                return monkeyTrueValue;
            } else {
                return monkeyFalseValue;
            }
        };
    }
}
