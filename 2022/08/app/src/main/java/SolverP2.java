import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class SolverP2 {

    public void solve(List<String> allLines) {
        var numRows = allLines.size();
        var numCols = allLines.get(0).length();

        var field = constructField(allLines, numRows, numCols);


        var scorePerTree = new ArrayList<BigInteger>();
        for (int row = 0; row < field.length; row++) {
            for (int col = 0; col < numCols; col++) {
                scorePerTree.add(scoreFromTree(field, row, col));
            }
        }

        Collections.sort(scorePerTree);
        System.out.printf("The highest scoring tree is %s\n",
                scorePerTree.get(scorePerTree.size() - 1).toString());
    }

    BigInteger scoreFromTree(Integer[][] field, int row, int col) {
        return scoreFromTheLeft(field, row, col)
                .multiply(scoreFromtheRight(field, row, col))
                        .multiply(scoreFromTheTop(field, row, col))
                                .multiply(scoreFromTheBottom(field, row, col));
    }

    BigInteger scoreFromTheBottom(Integer[][] field, int row, int col) {
        if (row == (field.length -1)) {
            return BigInteger.valueOf(0);
        }

        var allBottom =
                Arrays.stream(field)
                        .map(i -> i[col]).toList()
                        .subList(row + 1, field.length).stream().toList();

        var dist = IntStream.range(0, allBottom.size())
                .filter(idx -> allBottom.get(idx) >= field[row][col]).findFirst().orElse(allBottom.size() - 1);

        return BigInteger.valueOf(dist + 1);
    }

    BigInteger scoreFromTheTop(Integer[][] field, int row, int col) {
        if (row == 0) {
            return BigInteger.valueOf(0);
        }

        var allTop =
                new ArrayList<>(Arrays.stream(field).map(i -> i[col]).toList().subList(0, row));
        Collections.reverse(allTop);

        var dist = IntStream.range(0, allTop.size())
                .filter(idx -> allTop.get(idx) >= field[row][col]).findFirst().orElse(allTop.size() - 1);

        return BigInteger.valueOf(dist + 1);
    }

    BigInteger scoreFromtheRight(Integer[][] field, int row, int col) {
        if (col == (field[row].length - 1)) {
            return BigInteger.valueOf(0);
        }

        var allRight = Arrays.stream(field[row]).toList().subList(col + 1, field[row].length);

        var dist = IntStream.range(0, allRight.size())
                .filter(idx -> allRight.get(idx) >= field[row][col]).findFirst().orElse(allRight.size() - 1);

        return BigInteger.valueOf(dist + 1);
    }

    BigInteger scoreFromTheLeft(Integer[][] field, int row, int col) {
        if (col == 0) {
            return BigInteger.valueOf(0);
        }

        var allLeft = new ArrayList<>(Arrays.stream(field[row]).toList().subList(0, col));
        Collections.reverse(allLeft);

        var dist = IntStream.range(0, allLeft.size())
                .filter(idx -> allLeft.get(idx) >= field[row][col]).findFirst().orElse(allLeft.size() - 1);

        return BigInteger.valueOf(dist + 1);
    }

    Integer[][] constructField(List<String> allLines, int numRows, int numCols) {
        var field = new Integer[numRows][numCols];

        for (int row = 0; row < allLines.size(); row++) {
            for (int col = 0; col < numCols; col++) {
                var height = Integer.parseInt(allLines.get(row).charAt(col) + "");
                field[row][col] = height;
            }
        }

        return field;
    }
}
