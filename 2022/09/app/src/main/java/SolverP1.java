import helpers.CommandProcessor;
import models.RopeBoard;

import java.util.List;

public class SolverP1 {
    public void solve(List<String> allLines) {;
        var commands = allLines.stream().map(rawCommand -> {
            var splitCommand = rawCommand.split(" ");
            return CommandProcessor.processCommand(splitCommand);
        }).toList();

        var board = new RopeBoard(2);
        commands.forEach(board::moveHead);

        System.out.printf("The number of visited places by tail is %d\n", board.tailVisited(1));
    }
}
