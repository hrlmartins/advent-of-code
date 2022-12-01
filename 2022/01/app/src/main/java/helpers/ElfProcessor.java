package helpers;

import models.Elf;

import java.util.ArrayList;
import java.util.List;

public class ElfProcessor {
    
    public static List<Elf> getherElfs(List<String> elfsRaw) {
        List<Elf> container = new ArrayList<>(10);

        int caloriesTrack = 0;

        for (String calories : elfsRaw) {
            if (calories.isBlank() || calories.isEmpty()) {
                container.add(new Elf(caloriesTrack));
                caloriesTrack = 0;
                continue;
            }
            caloriesTrack += Integer.parseInt(calories);
        }

        return container.stream().toList();

    }
}
