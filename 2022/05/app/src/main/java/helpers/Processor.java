package helpers;

import models.MoveInstructions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Processor {
    private static final Pattern pattern = Pattern.compile("\\d+");

    public static List<List<String>> extractCrateList(int stacksCount, List<String> cratesOnly) {
        List<List<String>> crateList = new ArrayList<>(stacksCount + 1);
        for (int i = 0; i < stacksCount + 1; i++) {
            crateList.add(new ArrayList<>());
        }
        for (int j = 0; j < cratesOnly.size(); j++) {
            String s = cratesOnly.get(j);
            for (int i = 0; i < s.length(); i+=4) {
                if (crateList.get(j) == null) {
                    crateList.set(j, new ArrayList<>());
                }
                crateList.get(j).add(s.charAt(i + 1) + "");
            }
        }
        return crateList;
    }

    public static int extractNumberOfStacks(List<String> cratesRaw) {
        String stacksRow = cratesRaw.get(cratesRaw.size() - 1);
        Matcher cratesMatcher = pattern.matcher(stacksRow);
        int[] crateMovesIndexes = cratesMatcher.results().map(MatchResult::group).mapToInt(Integer::parseInt).toArray();
        return crateMovesIndexes[crateMovesIndexes.length - 1];
    }

    public static MoveInstructions extractMoveInstructions(String command) {
        var matcher = pattern.matcher(command);
        int[] crateMovesIndexes = matcher.results().map(MatchResult::group).mapToInt(Integer::parseInt).toArray();
        return new MoveInstructions(crateMovesIndexes[0], crateMovesIndexes[1], crateMovesIndexes[2]);
    }

    public static int findBreakPos(List<String> allLines) {
        for (int pos = 0; pos < allLines.size(); pos++) {
            if (allLines.get(pos).isEmpty()) {
                return pos;
            }
        }

        // should never reach
        return -1;
    }

}
