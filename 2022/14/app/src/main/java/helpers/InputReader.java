package helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InputReader {
    private final Scanner reader = new Scanner(System.in);

    public List<String> readAllLines() {
        List<String> input = new ArrayList<>(10);
        while (reader.hasNextLine()) {
            input.add(reader.nextLine());
        }

        return input.stream().toList();
    }
}
