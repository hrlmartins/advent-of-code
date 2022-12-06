import java.util.HashSet;
import java.util.List;

public class SolverP2 {

    private static final int WINDOW_SIZE = 14;
    public void solve(List<String> allLines) {
        // we only need the first liine as the input is a single sequence
        var signal = allLines.get(0);

        var signalCheck = new HashSet<Character>();
        for (int windowStartPos = 0; windowStartPos < signal.toCharArray().length; windowStartPos++) {
            for (int windowEndPos = windowStartPos; windowEndPos < windowStartPos + WINDOW_SIZE; windowEndPos++) {
                signalCheck.add(signal.charAt(windowEndPos));
            }

            if (signalCheck.size() == WINDOW_SIZE) {
                // we found the signal mark!!
                System.out.printf("First signal mark at: %d\n", windowStartPos + WINDOW_SIZE);
                break;
            }

            signalCheck.clear();
        }
    }
}
