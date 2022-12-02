import models.HandShape;
import models.Paper;
import models.Rock;
import models.Scissors;

import java.util.Arrays;
import java.util.List;

public class SolverP1 {
    public void solve(List<String> allLines) {
        var totalScore = allLines.stream().reduce(0, (sum, play) -> {
            if (play.isEmpty() || play.isBlank()) return sum;

            var playersPlay = processPlayersHand(Arrays.stream(play.split(" ")).toList());

            var playOutcome = playersPlay.get(1).against(playersPlay.get(0));

            return sum + switch (playOutcome) {
                case WIN -> 6 + playersPlay.get(1).getShapePoints();
                case DRAW -> 3 + playersPlay.get(1).getShapePoints();
                case DEFEAT -> playersPlay.get(1).getShapePoints();
            };

        }, Integer::sum);

        System.out.printf("The total score after cheating a bit is: %d\n", totalScore);
    }

    private List<HandShape> processPlayersHand(List<String> play) {
        // opponent is always the first position
        // player is the second position... the symbols are different

        return Arrays.asList(processOpponent(play.get(0)), processPlayer(play.get(1)));
    }

    /**
     *
     * Could have done all in the same method but separeted to be clearer
     *
     */
    private HandShape processOpponent(String handPlay) {
        if (handPlay.equals("A")) {
            return new Rock();
        } else if (handPlay.equals("B")) {
            return new Paper();
        } else {
            return new Scissors();
        }
    }

    private HandShape processPlayer(String handPlay) {
        if (handPlay.equals("X")) {
            return new Rock();
        } else if (handPlay.equals("Y")) {
            return new Paper();
        } else {
            return new Scissors();
        }
    }
}
