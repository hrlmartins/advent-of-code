import helpers.InputReader;

public class App {
    public static void main(String[] args) {
        var solver = new SolverP1();
        var solverTwo = new SolverP2();

        var reader = new InputReader();
        var allLines = reader.readAllLines();

        solver.solve(allLines);
        solverTwo.solve(allLines);
    }
}
