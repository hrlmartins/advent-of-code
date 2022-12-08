import java.util.Arrays;
import java.util.List;

public class SolverP1 {

    public void solve(List<String> allLines) {
        var numRows = allLines.size();
        var numCols = allLines.get(0).length();

        var field = constructField(allLines, numRows, numCols);


        var count = 0;
        for (int row = 0; row < field.length; row++) {
            for (int col = 0; col < numCols; col++) {
                if (positionIsVisible(field, row, col)) {
                    count++;
                }
            }
        }

        System.out.printf("The number of visible trees from the edges is %d\n", count);
    }

    boolean positionIsVisible(Integer[][] field, int row, int col) {
        return positionVisibleFromLeft(field, row, col) ||
                positionVisibleFromRight(field, row, col) ||
                positionVisibleFromTop(field, row, col) ||
                positionVisibleFromBottom(field, row, col);
    }

    boolean positionVisibleFromBottom(Integer[][] field, int row, int col) {
        if (row == (field.length -1)) {
            return true;
        }

        return Arrays.stream(field).map(i -> i[col]).toList()
                .subList(row + 1, field.length).stream().allMatch(height -> height < field[row][col]);
    }

    boolean positionVisibleFromTop(Integer[][] field, int row, int col) {
        if (row == 0) {
            return true;
        }

        return Arrays.stream(field).map(i -> i[col]).toList()
                        .subList(0, row).stream().allMatch(height -> height < field[row][col]);
    }

    boolean positionVisibleFromRight(Integer[][] field, int row, int col) {
         if (col == (field[row].length - 1)) {
             return true;
         }

         return Arrays.asList(field[row]).subList(col + 1, field[row].length)
                 .stream()
                 .allMatch(height -> height < field[row][col]);
    }

    private boolean positionVisibleFromLeft(Integer[][] field, int row, int col) {
        if (col == 0) {
            return true;
        }

        return Arrays.asList(field[row]).subList(0, col)
                .stream()
                .allMatch(height -> height < field[row][col]);
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
