import models.*;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class SolverP2 {
    public void solve(List<String> allLines) {

        var totalScore = allLines.stream().reduce(0, (sum, play) -> {
            if (play.isEmpty() || play.isBlank()) return sum;
            // first pos - opponents play
            // second pos - expected outcome
            var opponentPlayAndOutcome = Arrays.stream(play.split(" ")).toList();

            var opponentPlay = processOpponent(opponentPlayAndOutcome.get(0));
            var expectedPlayerOutcome = processWantedOutcome(opponentPlayAndOutcome.get(1));

            for (HandShape toPlay : Arrays.asList(new Rock(), new Scissors(), new Paper())) {
                if (toPlay.against(opponentPlay) == expectedPlayerOutcome) {
                    return sum + switch (expectedPlayerOutcome) {
                        case WIN -> 6 + toPlay.getShapePoints();
                        case DRAW -> 3 + toPlay.getShapePoints();
                        case DEFEAT -> toPlay.getShapePoints();
                    };
                }
            }

            return sum;
        }, Integer::sum);

        System.out.printf("Oppsy!! The total score after cheating (correctly) a bit is: %d\n", totalScore);
    }

    private HandShape processOpponent(String handPlay) {
        if (handPlay.equals("A")) {
            return new Rock();
        } else if (handPlay.equals("B")) {
            return new Paper();
        } else {
            return new Scissors();
        }
    }

    private PlayResult processWantedOutcome(String code) {
        if (code.equals("X")) {
            return PlayResult.DEFEAT;
        } else if (code.equals("Y")) {
            return PlayResult.DRAW;
        } else {
            return PlayResult.WIN;
        }
    }
}
