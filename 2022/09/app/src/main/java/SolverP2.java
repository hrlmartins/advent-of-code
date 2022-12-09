import helpers.CommandProcessor;
import models.RopeBoard;

import java.util.List;

public class SolverP2 {

    public void solve(List<String> allLines) {
        var commands = allLines.stream().map(rawCommand -> {
            var splitCommand = rawCommand.split(" ");
            return CommandProcessor.processCommand(splitCommand);
        }).toList();

        var board = new RopeBoard(10);
        commands.forEach(board::moveHead);

        System.out.printf("The number of visited places by some knot is %d\n", board.tailVisited(9));
    }
}
